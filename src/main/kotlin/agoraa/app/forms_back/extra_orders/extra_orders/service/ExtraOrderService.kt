package agoraa.app.forms_back.extra_orders.extra_orders.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.extra_orders.extra_order_products.service.ExtraOrderProductService
import agoraa.app.forms_back.extra_orders.extra_order_stores.service.ExtraOrderStoreService
import agoraa.app.forms_back.extra_orders.extra_orders.dto.request.ExtraOrderPatchRequest
import agoraa.app.forms_back.extra_orders.extra_orders.dto.request.ExtraOrderRequest
import agoraa.app.forms_back.extra_orders.extra_orders.dto.response.ExtraOrderResponse
import agoraa.app.forms_back.extra_orders.extra_orders.enums.OriginEnum
import agoraa.app.forms_back.extra_orders.extra_orders.enums.PartialCompleteEnum
import agoraa.app.forms_back.extra_orders.extra_orders.model.ExtraOrderModel
import agoraa.app.forms_back.extra_orders.extra_orders.repository.ExtraOrderRepository
import agoraa.app.forms_back.shared.exception.NotAllowedException
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
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
import java.time.LocalDateTime
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel

@Service
class ExtraOrderService(
    private val extraOrderRepository: ExtraOrderRepository,
    private val userService: UserService,
    private val extraOrderStoresService: ExtraOrderStoreService,
    private val extraOrderStoresProductsService: ExtraOrderProductService,
    private val supplierService: SupplierService

) {
    private fun validateSchema(schema: ExtraOrderRequest) {
        if (schema.partialComplete == PartialCompleteEnum.PARCIAL && (schema.products.isNullOrEmpty() || schema.stores.size != 1)) {
            throw IllegalArgumentException("Invalid Partial Extra Order")
        }
    }

    private fun createCriteria(
        username: String? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        partialCompleteEnum: PartialCompleteEnum? = null,
        origin: OriginEnum? = null,
        userId: Long? = null,
        supplierName: String? = null
    ): Specification<ExtraOrderModel> {
        return Specification { root: Root<ExtraOrderModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            userId?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            username?.let {
                predicates.add(criteriaBuilder.like(root.get<UserModel>("user").get("username"), "%$it%"))
            }

            createdAt?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("createdAt"), it))
            }

            processed?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("processed"), it))
            }

            partialCompleteEnum?.let {
                predicates.add(criteriaBuilder.equal(root.get<PartialCompleteEnum>("partialComplete"), it))
            }

            origin?.let {
                predicates.add(criteriaBuilder.equal(root.get<OriginEnum>("origin"), it))
            }
            supplierName?.let {
                predicates.add(criteriaBuilder.like(root.get<SupplierModel>("supplier").get("name"), "%$it%"))
                // val supplierJoin = root.join<ExtraOrderModel, agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel>("supplier")
                // predicates.add(criteriaBuilder.like(supplierJoin.get("name"), "%$it%"))
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        extraOrder: ExtraOrderModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = extraOrder.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(extraOrder: ExtraOrderModel, full: Boolean = false): ExtraOrderResponse {
        val userDto = userService.createDto(extraOrder.user)
        val extraOrderDto = ExtraOrderResponse(
            id = extraOrder.id,
            user = userDto,
            supplier = extraOrder.supplier,
            createdAt = extraOrder.createdAt,
            processed = extraOrder.processed,
            partialComplete = extraOrder.partialComplete,
            origin = extraOrder.origin,
        )

        if (full) {
            val extraOrderStores = extraOrderStoresService.findByParentId(extraOrder.id)
            val extraOrderProducts = extraOrderStoresProductsService.findByParentId(extraOrder.id)

            extraOrderDto.products = extraOrderProducts
            extraOrderDto.stores = extraOrderStores
        }

        return extraOrderDto
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ExtraOrderModel {
        val extraOrder = extraOrderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Extra Order with id $id not found") }

        return when {
            hasPermission(customUserDetails, extraOrder) -> extraOrder
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): ExtraOrderResponse {
        val extraOrder = findById(customUserDetails, id)

        return createDto(extraOrder, full)
    }

    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        username: String?,
        createdAt: LocalDateTime?,
        processed: Boolean?,
        partialComplete: PartialCompleteEnum?,
        origin: OriginEnum?,
        supplierName: String? = null
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, createdAt, processed, partialComplete, origin , supplierName = supplierName)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = extraOrderRepository.findAll(spec, pageable)

                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraOrders = extraOrderRepository.findAll(spec, sortBy)

                extraOrders.map { createDto(it, full) }
            }
        }
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        createdAt: LocalDateTime?,
        accepted: Boolean?,
        partialComplete: PartialCompleteEnum?,
        origin: OriginEnum?,
        supplierName: String? = null
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec =
            createCriteria(
                createdAt = createdAt,
                processed = accepted,
                partialCompleteEnum = partialComplete,
                origin = origin,
                userId = currentUser.id,
                supplierName = supplierName
            )

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = extraOrderRepository.findAll(spec, pageable)

                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraOrders = extraOrderRepository.findAll(spec, sortBy)

                extraOrders.map { createDto(it, full) }
            }
        }
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ExtraOrderRequest) {
        val currentUser = customUserDetails.getUserModel()
        val supplier = supplierService.findById(request.supplier.id)
        validateSchema(request)

        val extraOrder = extraOrderRepository.saveAndFlush(
            ExtraOrderModel(
                user = currentUser,
                supplier = supplier,
                partialComplete = request.partialComplete,
                origin = request.origin,
            )
        )

        extraOrderStoresService.editOrCreateOrDelete(extraOrder, request.stores)
        request.products?.let { extraOrderStoresProductsService.editOrCreateOrDelete(extraOrder, it) }
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraOrderRequest) {
        val extraOrder = findById(customUserDetails, id)
        val supplier = supplierService.findById(request.supplier.id)

        val extraOrderEdit = extraOrderRepository.saveAndFlush(
            extraOrder.copy(
                processed = request.processed,
                supplier = supplier,
                partialComplete = request.partialComplete,
                origin = request.origin,
            )
        )

        extraOrderStoresService.editOrCreateOrDelete(extraOrderEdit, request.stores)
        request.products?.let {
            extraOrderStoresProductsService.editOrCreateOrDelete(extraOrderEdit, it)
        }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val extraOrder = findById(customUserDetails, id)
        extraOrderRepository.delete(extraOrder)
    }

    fun patch(customUserDetails: CustomUserDetails, id: Long, request: ExtraOrderPatchRequest) {
        val extraOrder = findById(customUserDetails, id)

        extraOrderRepository.save(extraOrder.copy(processed = request.processed ?: extraOrder.processed))
    }
}
