package agoraa.app.forms_back.orders.orders.service

import agoraa.app.forms_back.orders.orders.dto.request.OrderPatchRequest
import agoraa.app.forms_back.orders.orders.dto.request.OrderRequest
import agoraa.app.forms_back.orders.orders.dto.response.OrderResponse
import agoraa.app.forms_back.orders.orders.model.OrderModel
import agoraa.app.forms_back.orders.orders.repository.OrderRepository
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import agoraa.app.forms_back.suppliers.suppliers.service.SupplierService
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
class OrderService(
    private val orderRepository: OrderRepository,
    private val supplierService: SupplierService
) {
    fun createDto(orderModel: OrderModel): OrderResponse {
        val orderResponse = OrderResponse(
            id = orderModel.id,
            supplier = orderModel.supplier,
            orderNumber = orderModel.orderNumber,
            store = orderModel.store,
            dateCreated = orderModel.dateCreated,
            deliveryDate = orderModel.deliveryDate,
            receivedDate = orderModel.receivedDate,
            issued = orderModel.issued,
            received = orderModel.received,
            totalValue = orderModel.totalValue,
            pendingValue = orderModel.pendingValue,
            receivedValue = orderModel.receivedValue
        )

        return orderResponse
    }

    private fun createCriteria(
        dateCreated: LocalDateTime? = null,
        orderNumber: Long? = null,
        store: StoresEnum? = null,
        issued: Boolean? = null,
        received: Boolean? = null,
        orderNumbers: List<Long>? = null,
    ): Specification<OrderModel> {
        return Specification { root: Root<OrderModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            dateCreated?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("dateCreated"), it))
            }

            orderNumber?.let {
                predicates.add(criteriaBuilder.equal(root.get<Long>("orderNumber"), it))
            }

            store?.let {
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("store"), it))
            }

            issued?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("issued"), it))
            }

            received?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("received"), it))
            }

            orderNumbers?.let {
                predicates.add(root.get<Long>("orderNumber").`in`(it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun findAll(
        dateCreated: LocalDateTime?,
        orderNumber: Long?,
        store: StoresEnum?,
        issued: Boolean?,
        received: Boolean?,
    ): List<OrderModel> {
        val spec = createCriteria(
            dateCreated = dateCreated,
            orderNumber = orderNumber,
            store = store,
            issued = issued,
            received = received
        )

        return orderRepository.findAll(spec)
    }

    fun getAll(
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        dateCreated: LocalDateTime?,
        orderNumber: Long?,
        store: StoresEnum?,
        issued: Boolean?,
        received: Boolean?,
    ): Any {
        val spec = createCriteria(
            dateCreated = dateCreated,
            orderNumber = orderNumber,
            store = store,
            issued = issued,
            received = received
        )
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = orderRepository.findAll(spec, pageable)
                PageImpl(
                    pageResult.content.map { productModel ->
                        createDto(productModel)
                    },
                    pageable,
                    pageResult.totalElements
                )
            }

            else -> {
                orderRepository.findAll(spec, sortBy).map { productModel ->
                    createDto(productModel)
                }
            }
        }
    }

    fun findById(id: Long): OrderModel {
        return orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found") }
    }

    fun returnById(id: Long): OrderResponse {
        return createDto(findById(id))
    }

    fun patch(id: Long, request: OrderPatchRequest) {
        val order = findById(id)

        orderRepository.save(
            order.copy(
                cancelIssued = request.cancelIssued ?: order.cancelIssued,
                cancelIssuedMotive = request.cancelIssuedMotive ?: order.cancelIssuedMotive,
            )
        )
    }

    @Transactional
    fun editOrCreateMultiple(request: List<OrderRequest>) {
        val suppliersNames = request.map { it.supplier }.distinct()
        val suppliers = supplierService.getAll(suppliersNames)

        if (suppliersNames.size != suppliers.size) throw IllegalArgumentException("One or more suppliers not found")

        val supplierMap = suppliers.associateBy { it.name }
        val spec = createCriteria(orderNumbers = request.map { it.orderNumber })
        val orderModels = orderRepository.findAll(spec)
        val ordersMap = orderModels.associateBy { it.orderNumber }

        val resultOrders = request.map { p ->
            val supp = supplierMap[p.supplier] ?: throw IllegalArgumentException("Supplier not Found")
            val existingOrder = ordersMap[p.orderNumber]

            existingOrder?.copy(
                store = p.store,
                supplier = supp,
                dateCreated = p.dateCreated,
                issued = p.issued,
                received = p.received,
                deliveryDate = p.deliveryDate,
                receivedDate = p.receivedDate,
                totalValue = p.totalValue,
                pendingValue = p.pendingValue,
                receivedValue = p.receivedValue
            )
                ?: OrderModel(
                    store = p.store,
                    supplier = supp,
                    orderNumber = p.orderNumber,
                    dateCreated = p.dateCreated,
                    issued = p.issued,
                    received = p.received,
                    deliveryDate = p.deliveryDate,
                    receivedDate = p.receivedDate,
                    totalValue = p.totalValue,
                    pendingValue = p.pendingValue,
                    receivedValue = p.receivedValue
                )
        }
        orderRepository.saveAll(resultOrders)
    }
}