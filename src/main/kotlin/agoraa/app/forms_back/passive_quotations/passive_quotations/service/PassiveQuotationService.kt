package agoraa.app.forms_back.passive_quotations.passive_quotations.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.suppliers.SupplierModel
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.service.PassiveQuotationProductsService
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationCalculateRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.response.PassiveQuotationCalculationResponse
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.response.PassiveQuotationResponse
import agoraa.app.forms_back.passive_quotations.passive_quotations.repository.PassiveQuotationRepository
import agoraa.app.forms_back.service.ProductService
import agoraa.app.forms_back.service.suppliers.SupplierService
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
import kotlin.math.max
import kotlin.math.round

@Service
class PassiveQuotationService(
    private val passiveQuotationRepository: PassiveQuotationRepository,
    private val passiveQuotationProductsService: PassiveQuotationProductsService,
    private val userService: UserService,
    private val supplierService: SupplierService,
    private val productsService: ProductService
) {
    private fun createCriteria(
        username: String? = null,
        supplier: String? = null,
        createdAt: LocalDateTime? = null,
        store: StoresEnum? = null,
        userId: Long? = null,
    ): Specification<agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel> {
        return Specification { root: Root<agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("store"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        passiveQuotation: agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = passiveQuotation.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(
        passiveQuotation: agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel,
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
        )

        if (full) {
            val passiveQuotationProducts = passiveQuotationProductsService.findByParentId(passiveQuotation.id)

            passiveQuotationDto.products = passiveQuotationProducts
        }

        return passiveQuotationDto
    }

    fun findById(
        customUserDetails: CustomUserDetails,
        id: Long
    ): agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel {
        val passiveQuotation = passiveQuotationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Passive Quotation with id $id not found") }

        return when {
            hasPermission(customUserDetails, passiveQuotation) -> passiveQuotation
            else -> throw NotAllowedException("You don't have permission to access this resource")
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
        store: StoresEnum?
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, supplier, createdAt, store)

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
        store: StoresEnum?
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
                userId = currentUser.id
            )

        val pageable = PageRequest.of(page, size, sortBy)
        val pageResult = passiveQuotationRepository.findAll(spec, pageable)
        return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
    }

    fun calculateQuotation(request: PassiveQuotationCalculateRequest): List<Map<String, PassiveQuotationCalculationResponse>> {
        val stores = StoresEnum.entries
        val requestCodes = request.products.map { it.code }
        val products = productsService.findAll(requestCodes)

        val notFundProducts = products.toSet() - requestCodes.toSet()

        if (notFundProducts.isNotEmpty()) {
            throw ResourceNotFoundException("Products with codes $notFundProducts not found")
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
                requestItem.price > (productStore.netCost!! * (1 - request.variation)) -> 2
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
            val salesNetStock = stockPlusOpenOrder < productStore.transferPackage * request.param4 ||
                    stockPlusOpenOrder > biggestSale * request.param4
            val mirrorQuantity = when {
                salesNetStock -> 0
                else -> when {
                    finalStock < productStore.packageQuantity * request.storesQuantity -> request.storesQuantity
                    else -> round(boxNecessity).toInt()
                }
            }
            val relevantParam = (request.param3 - list.size) * productStore.transferPackage
            val relevantPurchase = when {
                stockPlusOpenOrder < relevantParam -> true
                else -> {
                    when {
                        mirrorQuantity * productStore.packageQuantity / biggestSale < request.param6 -> false
                        else -> true
                    }
                }
            }
            val saleDay = biggestSale / 30
            val biggestExpiration = list.maxOf { it.expirationDate ?: LocalDate.now().plusYears(1) }
            val expirationDays = biggestExpiration.toEpochDay() - LocalDate.now().toEpochDay()
            val salesProjection = saleDay * expirationDays
            val finalQtt = requestItem.quantity ?: when {
                (biggestSale < (request.param1 * productStore.packageQuantity) && stockPlusOpenOrder > request.param2 * productStore.packageQuantity) || !relevantPurchase || salesNetStock -> 0
                else -> {
                    when {
                        finalStock < productStore.transferPackage * request.storesQuantity -> request.storesQuantity * productStore.transferPackage - stockPlusOpenOrder
                        else -> boxNecessity
                    }
                }
            }
            val maxPurchase = when {
                (salesProjection - currentStockSum) / productStore.packageQuantity <= 0 -> 0
                else -> (salesProjection - currentStockSum) / productStore.packageQuantity
            }
            val total = requestItem.price * finalQtt.toDouble() * productStore.packageQuantity
            val stockPlusOrder = stockPlusOpenOrder + finalQtt.toInt() + productStore.packageQuantity

            mapOf(
                code to PassiveQuotationCalculationResponse(
                    productStore.name,
                    productStore.packageQuantity,
                    biggestSale,
                    stockPlusOpenOrder,
                    list.find { it.store == StoresEnum.TRESMANN_VIX }!!.currentStock ?: 0.0,
                    list.find { it.store == StoresEnum.TRESMANN_SMJ }!!.currentStock ?: 0.0,
                    list.find { it.store == StoresEnum.TRESMANN_STT }!!.currentStock ?: 0.0,
                    finalQtt.toDouble(),
                    maxPurchase.toDouble(),
                    productStore.netCost!!,
                    productStore.averageExpiration,
                    total,
                    flag1,
                    flag2,
                    stockPlusOpenOrder == currentStockSum,
                    salesProjection > stockPlusOrder,
                    productStore.brand!!
                )
            )
        }

        return response
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: PassiveQuotationRequest) {
        val currentUser = customUserDetails.getUserModel()
        val supplier = supplierService.findById(request.supplier.id)

        val passiveQuotation = passiveQuotationRepository.saveAndFlush(
            agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel(
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
            )
        )
        passiveQuotationProductsService.editOrCreateOrDelete(editedPassiveQuotation, request.products)
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val passiveQuotation = findById(customUserDetails, id)
        passiveQuotationRepository.delete(passiveQuotation)
    }
}