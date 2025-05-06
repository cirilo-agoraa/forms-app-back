package agoraa.app.forms_back.orders.orders.service

import agoraa.app.forms_back.orders.orders.dto.request.OrderRequest
import agoraa.app.forms_back.orders.orders.dto.response.OrderResponse
import agoraa.app.forms_back.orders.orders.model.OrderModel
import agoraa.app.forms_back.orders.orders.repository.OrderRepository
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
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
    private val supplierService: SupplierService,
) {
    fun createDto(orderModel: OrderModel): OrderResponse {
        val orderResponse = OrderResponse(
            id = orderModel.id,
            supplier = orderModel.supplier,
            orderNumber = orderModel.orderNumber,
            buyer = orderModel.buyer,
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
        dateCreatedEquals: LocalDateTime? = null,
        orderNumberEquals: Long? = null,
        storeEquals: StoresEnum? = null,
        issuedEquals: Boolean? = null,
        receivedEquals: Boolean? = null,
        orderNumbersIn: List<Long>? = null,
        supplierNameEquals: String? = null,
    ): Specification<OrderModel> {
        return Specification { root: Root<OrderModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            dateCreatedEquals?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("dateCreated"), it))
            }

            orderNumberEquals?.let {
                predicates.add(criteriaBuilder.equal(root.get<Long>("orderNumber"), it))
            }

            storeEquals?.let {
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("store"), it))
            }

            issuedEquals?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("issued"), it))
            }

            receivedEquals?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("received"), it))
            }

            orderNumbersIn?.let {
                predicates.add(root.get<Long>("orderNumber").`in`(it))
            }

            supplierNameEquals?.let {
                predicates.add(criteriaBuilder.equal(root.get<SupplierModel>("supplier").get<String>("name"), it))
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
            dateCreatedEquals = dateCreated,
            orderNumberEquals = orderNumber,
            storeEquals = store,
            issuedEquals = issued,
            receivedEquals = received
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
        supplierName: String?
    ): Any {
        val spec = createCriteria(
            dateCreatedEquals = dateCreated,
            orderNumberEquals = orderNumber,
            storeEquals = store,
            issuedEquals = issued,
            receivedEquals = received,
            supplierNameEquals = supplierName
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

    @Transactional
    fun editOrCreateMultiple(request: List<OrderRequest>) {
        val suppliersNames = request.map { it.supplier }.distinct()
        val suppliers = supplierService.getAll(suppliersNames)

        if (suppliersNames.size != suppliers.size) throw IllegalArgumentException("One or more suppliers not found")

        val supplierMap = suppliers.associateBy { it.name }
        val spec = createCriteria(orderNumbersIn = request.map { it.orderNumber })
        val orderModels = orderRepository.findAll(spec)
        val ordersMap = orderModels.associateBy { it.orderNumber }

        val resultOrders = request.map { p ->
            val supp = supplierMap[p.supplier] ?: throw IllegalArgumentException("Supplier not Found")
            val existingOrder = ordersMap[p.orderNumber]

            existingOrder?.copy(
                store = p.store,
                supplier = supp,
                buyer = p.buyer,
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
                    buyer = p.buyer,
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