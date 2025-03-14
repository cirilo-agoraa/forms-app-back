package agoraa.app.forms_back.service.extra_orders

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.extra_order.ExtraOrderDto
import agoraa.app.forms_back.enum.extra_order.OriginEnum
import agoraa.app.forms_back.enum.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.model.extra_orders.ExtraOrderModel
import agoraa.app.forms_back.repository.extra_orders.ExtraOrderRepository
import agoraa.app.forms_back.schema.extra_order.ExtraOrderCreateSchema
import agoraa.app.forms_back.schema.extra_order.ExtraOrderEditSchema
import agoraa.app.forms_back.service.UserService
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

@Service
class ExtraOrderService(
    private val extraOrderRepository: ExtraOrderRepository,
    private val userService: UserService,
    private val extraOrderStoresService: ExtraOrderStoreService,
    private val extraOrderStoresProductsService: ExtraOrderProductService,
) {
    private fun validateSchema(schema: ExtraOrderCreateSchema) {
        if (schema.partialComplete == "PARCIAL" && (schema.products.isNullOrEmpty() || schema.stores.size != 1)) {
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

    fun createDto(extraOrder: ExtraOrderModel, full: Boolean = false): ExtraOrderDto {
        val userDto = userService.createDto(extraOrder.user)
        val extraOrderDto = ExtraOrderDto(
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
    ): ExtraOrderDto {
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
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, createdAt, processed, partialComplete, origin)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = extraOrderRepository.findAll(spec, pageable)

                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraorders = extraOrderRepository.findAll(spec, sortBy)

                extraorders.map { createDto(it, full) }
            }
        }
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        createdAt: LocalDateTime?,
        accepted: Boolean?,
        partialComplete: PartialCompleteEnum?,
        origin: OriginEnum?,
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
                userId = currentUser.id
            )

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = extraOrderRepository.findAll(spec, pageable)

                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraOrders = extraOrderRepository.findAll(spec, sortBy)

                extraOrders.map { createDto(it) }
            }
        }
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ExtraOrderCreateSchema) {
        val currentUser = customUserDetails.getUserModel()
        validateSchema(request)

        val extraOrder = extraOrderRepository.saveAndFlush(
            ExtraOrderModel(
                user = currentUser,
                supplier = request.supplier,
                partialComplete = PartialCompleteEnum.valueOf(request.partialComplete),
                origin = request.origin?.let { OriginEnum.valueOf(it) },
            )
        )

        extraOrderStoresService.create(extraOrder, request.stores)
        request.products?.let { extraOrderStoresProductsService.create(extraOrder, it) }
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraOrderEditSchema) {
        val extraOrder = findById(customUserDetails, id)

        val extraOrderEdit = extraOrderRepository.saveAndFlush(
            extraOrder.copy(
                processed = request.processed ?: extraOrder.processed,
                supplier = request.supplier ?: extraOrder.supplier,
                partialComplete = request.partialComplete?.let { PartialCompleteEnum.valueOf(it) }
                    ?: extraOrder.partialComplete,
                origin = request.origin?.let { OriginEnum.valueOf(it) } ?: extraOrder.origin,
            )
        )

        request.stores?.let { extraOrderStoresService.edit(extraOrderEdit, it) }
        request.products?.let {
            extraOrderStoresProductsService.edit(extraOrderEdit, it)
        }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val extraOrder = findById(customUserDetails, id)
        extraOrderRepository.delete(extraOrder)
    }

}
