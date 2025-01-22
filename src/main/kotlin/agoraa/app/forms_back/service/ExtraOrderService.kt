package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.extra_order.ExtraOrderDto
import agoraa.app.forms_back.enum.authority.AuthorityTypeEnum
import agoraa.app.forms_back.enum.extra_order.OriginEnum
import agoraa.app.forms_back.enum.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderModel
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.ExtraOrderRepository
import agoraa.app.forms_back.schema.extra_order.*
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

@Service
class ExtraOrderService(
    private val extraOrderRepository: ExtraOrderRepository,
    private val supplierService: SupplierService,
    private val extraOrderProductService: ExtraOrderProductService,
    private val extraOrderStoreService: ExtraOrderStoreService
) {

    private fun createCriteria(
        supplierId: Long?,
        supplierName: String?,
        userId: Long?,
        userName: String?,
        processed: Boolean?,
        dateSubmitted: String?,
        origin: String?,
        partialComplete: PartialCompleteEnum?,
    ): Specification<ExtraOrderModel> {
        return Specification { root: Root<ExtraOrderModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            supplierId?.let {
                predicates.add(criteriaBuilder.equal(root.get<SupplierModel>("supplier").get<Long>("id"), it))
            }

            supplierName?.let {
                predicates.add(criteriaBuilder.like(root.get<SupplierModel>("supplier").get<String>("name"), "%$it%"))
            }

            userId?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            userName?.let {
                predicates.add(criteriaBuilder.like(root.get<UserModel>("user").get("username"), "%$it%"))
            }

            processed?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("processed"), it))
            }

            dateSubmitted?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDate>("dateSubmitted"), LocalDate.parse(it)))
            }

            origin?.let {
                predicates.add(criteriaBuilder.equal(root.get<OriginEnum>("origin"), OriginEnum.valueOf(it)))
            }

            partialComplete?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<PartialCompleteEnum>("partialComplete"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun createDTO(extraOrder: ExtraOrderModel): ExtraOrderDto {
        return ExtraOrderDto(
            id = extraOrder.id,
            user = extraOrder.user.username,
            supplier = extraOrder.supplier.name,
            partialComplete = extraOrder.partialComplete,
            processed = extraOrder.processed,
            dateSubmitted = extraOrder.dateSubmitted.toString(),
            stores = extraOrder.stores.map { store -> store.store.name },
            products = extraOrder.products,
            origin = extraOrder.origin
        )
    }

    private fun validateCreateRequest(request: ExtraOrderCreateSchema) {
        if (request.partialComplete == "PARCIAL") {
            require(request.stores.size == 1) { "For PARCIAL orders, there must be exactly one store." }
            require(!request.products.isNullOrEmpty()) { "For PARCIAL orders, products cannot be null or empty." }
            require(request.origin != null) { "For PARCIAL orders, origin cannot be null." }
        }
    }

    fun findAll(
        pagination: Boolean,
        convertToDTO: Boolean,
        supplierId: Long?,
        supplierName: String?,
        userId: Long?,
        userName: String?,
        processed: Boolean?,
        dateSubmitted: String?,
        origin: String?,
        partialComplete: PartialCompleteEnum?,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val spec = createCriteria(
            supplierId,
            supplierName,
            userId,
            userName,
            processed,
            dateSubmitted,
            origin,
            partialComplete
        )
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        var extraOrders = extraOrderRepository.findAll(spec, sortBy)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val start = pageable.offset.toInt()
                val end = (start + pageable.pageSize).coerceAtMost(extraOrders.size)

                if (convertToDTO) {
                    PageImpl(extraOrders.subList(start, end).map { createDTO(it) }, pageable, extraOrders.size.toLong())
                } else {
                    PageImpl(extraOrders.subList(start, end), pageable, extraOrders.size.toLong())
                }
            }

            else -> {
                if (convertToDTO) {
                    extraOrders.map { createDTO(it) }
                } else {
                    extraOrders
                }
            }
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ExtraOrderModel {
        val user = customUserDetails.getUserModel()
        return extraOrderRepository.findById(id)
            .map { extraOrder ->
                if (extraOrder.user.id == user.id || customUserDetails.authorities.map { it.authority }
                        .contains("ROLE_ADMIN")) {
                    extraOrder
                } else {
                    throw NotAllowedException("You are not allowed to access this resource")
                }
            }
            .orElseThrow { throw ResourceNotFoundException("Extra Order not Found") }
    }

    fun returnById(customUserDetails: CustomUserDetails, id: Long, convertToDTO: Boolean): Any {
        val extraOrder = findById(customUserDetails, id)
        return if (convertToDTO) createDTO(extraOrder) else extraOrder
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraOrderEditSchema): ExtraOrderDto {
        val extraOrder = findById(customUserDetails, id)
        val partialComplete =
            request.partialComplete?.let { PartialCompleteEnum.valueOf(it) } ?: extraOrder.partialComplete

        val updatedOrder = extraOrder.copy(
            partialComplete = partialComplete,
            supplier = request.supplier?.let { supplierService.findById(it.id) } ?: extraOrder.supplier,
            processed = request.processed ?: extraOrder.processed,
            stores = request.stores?.let { extraOrderStoreService.edit(extraOrder, it) } ?: extraOrder.stores,
        )

        if (partialComplete == PartialCompleteEnum.PARCIAL) {
            updatedOrder.products =
                request.products?.let { extraOrderProductService.edit(extraOrder, it) } ?: extraOrder.products
            updatedOrder.origin = request.origin?.let { OriginEnum.valueOf(it) } ?: extraOrder.origin

        } else {
            extraOrder.products = mutableListOf()
            updatedOrder.origin = null
        }

        extraOrderRepository.save(extraOrder)

        return createDTO(extraOrder)
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, convertToDTO: Boolean, request: ExtraOrderCreateSchema): Any {
        validateCreateRequest(request)

        val user = customUserDetails.getUserModel()
        val supplier = supplierService.findById(request.supplierId)

        val extraOrder = ExtraOrderModel(
            user = user,
            supplier = supplier,
            partialComplete = PartialCompleteEnum.valueOf(request.partialComplete),
        )

        val stores = extraOrderStoreService.create(extraOrder, request.stores)
        extraOrder.stores.addAll(stores)

        if (extraOrder.partialComplete == PartialCompleteEnum.PARCIAL) {
            val products = extraOrderProductService.create(extraOrder, request.products!!)
            extraOrder.products.addAll(products)
            extraOrder.origin = OriginEnum.valueOf(request.origin!!)
        }

        val createdExtraOrder = extraOrderRepository.save(extraOrder)

        return if (convertToDTO) createDTO(createdExtraOrder) else createdExtraOrder
    }
}
