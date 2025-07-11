package agoraa.app.forms_back.passive_quotations.passive_quotations.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.service.PassiveQuotationProductsService
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationCalculateRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationPatchRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationPrintRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.response.PassiveQuotationCalculationResponse
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.response.PassiveQuotationResponse
import agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel
import agoraa.app.forms_back.passive_quotations.passive_quotations.repository.PassiveQuotationRepository
import agoraa.app.forms_back.products.products.service.ProductService
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.service.ChatsacService
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import agoraa.app.forms_back.suppliers.suppliers.service.SupplierService
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.users.users.service.UserService
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit
import org.springframework.scheduling.annotation.Scheduled
import java.time.Duration

@Service
class PassiveQuotationService(
    private val passiveQuotationRepository: PassiveQuotationRepository,
    private val passiveQuotationProductsService: PassiveQuotationProductsService,
    private val userService: UserService,
    private val supplierService: SupplierService,
    private val productsService: ProductService,
    private val chatsacService: ChatsacService
) {
    private fun createCriteria(
        username: String? = null,
        supplier: String? = null,
        createdAt: LocalDateTime? = null,
        store: StoresEnum? = null,
        createOrder: Boolean? = null,
        userId: Long? = null,
    ): Specification<PassiveQuotationModel> {
        return Specification { root: Root<PassiveQuotationModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            userId?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            username?.let {
                predicates.add(criteriaBuilder.like(root.get<UserModel>("user").get("username"), "%$it%"))
            }

            supplier?.let {
                predicates.add(criteriaBuilder.like(root.get<SupplierModel>("supplier").get("name"), "%$it%"))
            }

            createdAt?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("createdAt"), it))
            }

            store?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<StoresEnum>("store"),
                        it
                    )
                )
            }

            createOrder?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<Boolean>("createOrder"),
                        it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        passiveQuotation: PassiveQuotationModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = passiveQuotation.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(
        passiveQuotation: PassiveQuotationModel,
        full: Boolean = false
    ): PassiveQuotationResponse {
        val userDto = userService.createDto(passiveQuotation.user)
        val passiveQuotationDto = PassiveQuotationResponse(
            id = passiveQuotation.id,
            user = userDto,
            createdAt = passiveQuotation.createdAt,
            store = passiveQuotation.store,
            supplier = passiveQuotation.supplier,
            paymentTerm = passiveQuotation.paymentTerm,
            variation = passiveQuotation.variation,
            worstTerm = passiveQuotation.worstTerm,
            storesQuantity = passiveQuotation.storesQuantity,
            param1 = passiveQuotation.param1,
            param2 = passiveQuotation.param2,
            param3 = passiveQuotation.param3,
            param4 = passiveQuotation.param4,
            param5 = passiveQuotation.param5,
            param6 = passiveQuotation.param6,
            param7 = passiveQuotation.param7,
            param8 = passiveQuotation.param8,
            bestTerm = passiveQuotation.bestTerm,
            wppGroup = passiveQuotation.wppGroup,
            createOrder = passiveQuotation.createOrder,
            status = statusToString(passiveQuotation.status) // <-- Aqui retorna a string!
        )

        if (full) {
            val passiveQuotationProducts = passiveQuotationProductsService.findByParentId(passiveQuotation.id)

            passiveQuotationDto.products =
                passiveQuotationProducts.map { passiveQuotationProductsService.createDto(it) }
        }

        return passiveQuotationDto
    }

    fun findById(
        customUserDetails: CustomUserDetails,
        id: Long
    ): PassiveQuotationModel {
        val passiveQuotation = passiveQuotationRepository.findById(id)
            .orElseThrow { agoraa.app.forms_back.shared.exception.ResourceNotFoundException("Passive Quotation with id $id not found") }

        return when {
            hasPermission(customUserDetails, passiveQuotation) -> passiveQuotation
            else -> throw agoraa.app.forms_back.shared.exception.NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): PassiveQuotationResponse {
        val passiveQuotation = findById(customUserDetails, id)
        return createDto(passiveQuotation, full)
    }
    private fun statusToString(status: Int?): String =
    when (status) {
        1 -> "Aprovada"
        2 -> "Recusada"
        else -> "Pendente"
    }
    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        username: String?,
        supplier: String?,
        createdAt: LocalDateTime?,
        store: StoresEnum?,
        createOrder: Boolean?,
        status: Int? = null
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, supplier, createdAt, store, createOrder)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = passiveQuotationRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val passiveQuotations = passiveQuotationRepository.findAll(spec, sortBy)

                passiveQuotations.map { createDto(it, full) }
            }
        }
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        supplier: String?,
        createdAt: LocalDateTime?,
        store: StoresEnum?,
        createOrder: Boolean?
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec =
            createCriteria(
                supplier = supplier,
                createdAt = createdAt,
                store = store,
                createOrder = createOrder,
                userId = currentUser.id
            )

        val pageable = PageRequest.of(page, size, sortBy)
        val pageResult = passiveQuotationRepository.findAll(spec, pageable)
        return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
    }

    fun calculateQuotation(request: PassiveQuotationCalculateRequest): List<PassiveQuotationCalculationResponse> {
        val requestCodes = request.products.map { it.code }
        val products = productsService.findAll(requestCodes)

        if (products.isEmpty()) {
            throw agoraa.app.forms_back.shared.exception.ResourceNotFoundException("Products not found")
        }

        val notFundProducts = products.map { it.code }.toSet() - requestCodes.toSet()

        if (notFundProducts.isNotEmpty()) {
            throw agoraa.app.forms_back.shared.exception.ResourceNotFoundException("Products with codes $notFundProducts not found")
        }

        val requestProductsMap = request.products.associateBy { it.code }
        val productsGrouped = products.groupBy { it.code }
        val response = productsGrouped.map { (code, list) ->
            val requestItem = requestProductsMap[code]!!
            val storeMap = list.associateBy { it.store }
            val productStore = storeMap[request.store] ?: list.first()
            val salesLastTwelveMonthsSum = list.sumOf { it.salesLastTwelveMonths }
            val salesLastThirtyDaysSum = list.sumOf { it.salesLastThirtyDays }
            val salesLastTwelveMonthsDivTwelve = salesLastTwelveMonthsSum / 12
            val currentStockSum = list.sumOf { it.currentStock ?: 0.0 }
            val openOrderSum = list.sumOf { it.openOrder }
            val biggestSale = max(salesLastTwelveMonthsDivTwelve, salesLastThirtyDaysSum)
            val stockPlusOpenOrder = requestItem.stockPlusOpenOrder ?: (currentStockSum + openOrderSum)
            val salesDay = (stockPlusOpenOrder / (biggestSale / 30))
            val flag1 = when {
                productStore.netCost == null || productStore.netCost == 0.0 -> 1
                requestItem.price < (productStore.netCost!! * (1 - request.variation)) -> 4
                requestItem.price == productStore.netCost!! -> 3
                requestItem.price > (productStore.netCost!! * (1 + request.variation)) -> 2
                else -> 3
            }
            val flag2 = when {
                requestItem.price > (productStore.netCost!! * request.param7) || requestItem.price < (productStore.netCost!! * request.param8) -> 2
                else -> 0
            }
            val unityNecessity = when {
                flag1 >= request.param5 -> {
                    when {
                        (request.bestTerm - salesDay) * (biggestSale / 30) < 0.0 -> 0.0
                        else -> (request.bestTerm - salesDay) * (biggestSale / 30)
                    }
                }

                else -> {
                    when {
                        (request.worstTerm - salesDay) * (biggestSale / 30) < 0.0 -> 0.0
                        else -> (request.worstTerm - salesDay) * (biggestSale / 30)
                    }
                }
            }
            val boxNecessity = (unityNecessity / productStore.packageQuantity)
            val finalStock = boxNecessity * productStore.packageQuantity + stockPlusOpenOrder
            val salesNetStock = when {
                stockPlusOpenOrder < productStore.transferPackage * request.param4 -> false
                else -> when {
                    stockPlusOpenOrder > biggestSale.toInt() * request.param4 -> true
                    else -> false
                }
            }
            val mirrorQuantity = when {
                salesNetStock -> 0
                else -> when {
                    finalStock < productStore.packageQuantity * request.storesQuantity -> request.storesQuantity
                    else -> ceil(boxNecessity).toInt()
                }
            }
            val currentStocks = list.map { it.currentStock ?: 0.0 }
            val tolerance = 1e-9
            val zeroOrNegativeStock = currentStocks.count { it < -tolerance || abs(it - 0.0) < tolerance }
            val relevantParam =
                (request.param3 - zeroOrNegativeStock) * productStore.transferPackage
            val relevantPurchase = when {
                stockPlusOpenOrder < relevantParam -> true
                else -> {
                    when {
                        mirrorQuantity * productStore.packageQuantity / biggestSale < request.param6 -> false
                        else -> true
                    }
                }
            }
            val saleDay = salesLastTwelveMonthsDivTwelve / 30
            val biggestValidity = list.maxOf { it.expirationDate ?: LocalDate.now().plusYears(1) }
            val expirationDays =
                (biggestValidity.toEpochDay() - LocalDate.now().toEpochDay()).days.toInt(unit = DurationUnit.DAYS)
            val salesProjection = saleDay * expirationDays
            val maxPurchase = when {
                (salesProjection - currentStockSum) / productStore.packageQuantity <= 0 -> 0
                else -> floor((salesProjection - currentStockSum) / productStore.packageQuantity)
            }
            var finalQtt = requestItem.quantity ?: when {
                (biggestSale < (request.param1 * productStore.packageQuantity) && stockPlusOpenOrder > request.param2 * productStore.packageQuantity) || !relevantPurchase || salesNetStock -> 0
                else -> {
                    when {
                        finalStock < productStore.transferPackage * request.storesQuantity -> request.storesQuantity * productStore.transferPackage - stockPlusOpenOrder
                        else -> boxNecessity
                    }
                }
            }
            finalQtt = ceil(finalQtt.toDouble())
            val total = requestItem.price * finalQtt * productStore.packageQuantity
            val stockPlusOrder = stockPlusOpenOrder + finalQtt * productStore.packageQuantity

            PassiveQuotationCalculationResponse(
                productStore,
                biggestSale.toInt(),
                stockPlusOpenOrder,
                list.find { it.store == agoraa.app.forms_back.shared.enums.StoresEnum.TRESMANN_VIX }!!.currentStock
                    ?: 0.0,
                list.find { it.store == agoraa.app.forms_back.shared.enums.StoresEnum.TRESMANN_SMJ }!!.currentStock
                    ?: 0.0,
                list.find { it.store == agoraa.app.forms_back.shared.enums.StoresEnum.TRESMANN_STT }!!.currentStock
                    ?: 0.0,
                finalQtt,
                maxPurchase.toDouble(),
                total,
                flag1,
                flag2,
                stockPlusOpenOrder != currentStockSum,
                !(salesProjection > stockPlusOrder),
            )
        }

        return response
    }

    fun sendPdf(request: PassiveQuotationPrintRequest) {
        val filePath = "F:\\COMPRAS\\Automações.Compras\\Cotacoes\\${request.fileName}.pdf"
        val groupId = request.wppGroup.getGroupId()

        print(chatsacService.sendPdf(filePath, groupId).block())
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: PassiveQuotationRequest) {
        val currentUser = customUserDetails.getUserModel()
        val supplier = supplierService.findById(request.supplier.id)

        val passiveQuotation = passiveQuotationRepository.saveAndFlush(
            PassiveQuotationModel(
                user = currentUser,
                supplier = supplier,
                wppGroup = request.wppGroup,
                store = request.store,
                paymentTerm = request.paymentTerm,
                variation = request.variation,
                worstTerm = request.worstTerm,
                storesQuantity = request.storesQuantity,
                param1 = request.param1,
                param2 = request.param2,
                param3 = request.param3,
                param4 = request.param4,
                param5 = request.param5,
                param6 = request.param6,
                param7 = request.param7,
                param8 = request.param8,
                bestTerm = request.bestTerm,
            )
        )

        passiveQuotationProductsService.editOrCreateOrDelete(passiveQuotation, request.products)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: PassiveQuotationRequest) {
        val passiveQuotation = findById(customUserDetails, id)
        val supplier = supplierService.findById(request.supplier.id)

        val editedPassiveQuotation = passiveQuotationRepository.saveAndFlush(
            passiveQuotation.copy(
                supplier = supplier,
                wppGroup = request.wppGroup,
                store = request.store,
                paymentTerm = request.paymentTerm,
                variation = request.variation,
                worstTerm = request.worstTerm,
                storesQuantity = request.storesQuantity,
                param1 = request.param1,
                param2 = request.param2,
                param3 = request.param3,
                param4 = request.param4,
                param5 = request.param5,
                param6 = request.param6,
                param7 = request.param7,
                param8 = request.param8,
                bestTerm = request.bestTerm,
                createOrder = request.createOrder
            )
        )
        passiveQuotationProductsService.editOrCreateOrDelete(editedPassiveQuotation, request.products)
    }

    fun patch(customUserDetails: CustomUserDetails, id: Long, request: PassiveQuotationPatchRequest) {
        val extraQuotation = findById(customUserDetails, id)
        println("Extra Quotation: $extraQuotation")
        println("Request: $request")


        passiveQuotationRepository.save(
            extraQuotation.copy(
                createOrder = request.createOrder,
                status = request.status ?: extraQuotation.status 
            )
        )
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val passiveQuotation = findById(customUserDetails, id)
        passiveQuotationRepository.delete(passiveQuotation)
    }

    @Scheduled(fixedDelay = 3_600_000)
    fun notifyPendingQuotations() {
        val now = LocalDateTime.now()
        val hour = now.hour
        if (hour < 8 || hour >= 18) return // Só executa entre 8h (inclusive) e 18h (exclusive)

        val pendingQuotations = passiveQuotationRepository.findAll()
            .filter { it.status == 0 && Duration.between(it.createdAt, now).toMinutes() >= 60 }

        pendingQuotations.forEach { quotation ->
            val msg = "COTAÇÃO FORNECEDOR ${quotation.supplier.name} PENDENTE há mais de uma hora"
            chatsacService.sendMsg(msg, "6749b7eb8f2a5e3014639a2c").subscribe()
        }
    }
}