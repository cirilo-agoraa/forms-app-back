package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enums.authority.AuthorityTypeEnum
import agoraa.app.forms_back.enums.extra_order.OriginEnum
import agoraa.app.forms_back.enums.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.exceptions.NotAllowedException
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
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
    private val userService: UserService,
    private val extraOrderProductService: ExtraOrderProductService,
    private val extraOrderStoreService: ExtraOrderStoreService
) {

    private fun createCriteria(
        supplier: Long?,
        user: Long?,
        processed: Boolean?,
        dateSubmitted: String?,
        origin: String?,
        partialComplete: PartialCompleteEnum?,
    ): Specification<ExtraOrderModel> {
        return Specification { root: Root<ExtraOrderModel>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            supplier?.let {
                predicates.add(criteriaBuilder.equal(root.get<SupplierModel>("supplier").get<Long>("id"), it))
            }

            user?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
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
                        root.get<PartialCompleteEnum>("partialComplete"), it)
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun createExtraOrderDTO(extraOrder: ExtraOrderModel): ExtraOrderDTO {
        return ExtraOrderDTO(
            id = extraOrder.id,
            user = extraOrder.user.username,
            supplier = extraOrder.supplier.name,
            partialComplete = extraOrder.partialComplete,
            processed = extraOrder.processed,
            dateSubmitted = extraOrder.dateSubmitted,
            stores = extraOrder.stores.map { store -> store.store.name },
            products = extraOrder.products.map {
                ExtraOrderProductsDTO(
                    code = it.product.code,
                    price = it.price,
                    quantity = it.quantity
                )
            },
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
        customUserDetails: CustomUserDetails,
        pagination: Boolean,
        supplier: Long?,
        user: Long?,
        processed: Boolean?,
        dateSubmitted: String?,
        origin: String?,
        partialComplete: PartialCompleteEnum?,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val spec = createCriteria(supplier, user, processed, dateSubmitted, origin, partialComplete)

        return if (pagination) {
            val sortDirection =
                if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
            val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
            val extraOrderPage = extraOrderRepository.findAll(spec, pageable)
            val extraOrderDTOs = extraOrderPage.content.map { createExtraOrderDTO(it) }

            if (!currentUser.authorities.any { it.authority == AuthorityTypeEnum.ROLE_ADMIN }) {
                val extraOrderDTOsFiltered = extraOrderDTOs.filter { it.user == currentUser.username }
                val start = pageable.offset.toInt()
                val end = (start + pageable.pageSize).coerceAtMost(extraOrderDTOsFiltered.size)
                return PageImpl(extraOrderDTOsFiltered.subList(start, end), pageable, extraOrderDTOsFiltered.size.toLong())
            }

            PageImpl(extraOrderDTOs, pageable, extraOrderPage.totalElements)
        } else {
            val extraOrders = extraOrderRepository.findAll(spec)
            val extraOrderDTOs = extraOrders.map { createExtraOrderDTO(it) }

            if (!currentUser.authorities.any { it.authority == AuthorityTypeEnum.ROLE_ADMIN }) {
                return extraOrderDTOs.filter { it.user == currentUser.username }
            }

            extraOrderDTOs
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

    fun returnById(customUserDetails: CustomUserDetails, id: Long): ExtraOrderDTO {
        val extraOrder = findById(customUserDetails, id)
        return createExtraOrderDTO(extraOrder)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraOrderEditSchema): ExtraOrderDTO {
        val extraOrder = findById(customUserDetails, id)
        val partialComplete = request.partialComplete?.let { PartialCompleteEnum.valueOf(it) } ?: extraOrder.partialComplete

        val updatedOrder = extraOrder.copy(
            partialComplete = partialComplete,
            supplier = request.supplier?.let { supplierService.findById(it.id) } ?: extraOrder.supplier,
            processed = request.processed ?: extraOrder.processed,
            stores = request.stores?.let { extraOrderStoreService.edit(extraOrder, it) } ?: extraOrder.stores,
        )

        if (partialComplete == PartialCompleteEnum.PARCIAL) {
            updatedOrder.products = request.products?.let { extraOrderProductService.edit(extraOrder, it)} ?: extraOrder.products
            updatedOrder.origin =  request.origin?.let { OriginEnum.valueOf(it) } ?: extraOrder.origin

        } else {
            extraOrder.products = mutableListOf()
            updatedOrder.origin = null
        }

        extraOrderRepository.save(extraOrder)

        return createExtraOrderDTO(extraOrder)
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ExtraOrderCreateSchema): ExtraOrderDTO {
        validateCreateRequest(request)

        val user = customUserDetails.getUserModel()
        val supplier = supplierService.findById(request.supplier.id)

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

        extraOrderRepository.save(extraOrder)
        return createExtraOrderDTO(extraOrder)
    }
}
