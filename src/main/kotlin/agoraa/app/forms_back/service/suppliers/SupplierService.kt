package agoraa.app.forms_back.service.suppliers

import agoraa.app.forms_back.dto.suppliers.SupplierDto
import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.suppliers.SupplierModel
import agoraa.app.forms_back.repository.suppliers.SupplierRepository
import agoraa.app.forms_back.schema.supplier.SupplierSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SupplierService(
    private val supplierRepository: SupplierRepository,
    private val supplierStoresService: SupplierStoresService
) {

    private fun createCriteria(
        name: String? = null,
        exchange: Boolean? = null,
        status: List<SupplierStatusEnum>? = null,
        names: List<String>? = null
    ): Specification<SupplierModel> {
        return Specification { root: Root<SupplierModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            name?.let {
                predicates.add(criteriaBuilder.like(root.get("name"), "%$it%"))
            }

            exchange?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("exchange"), it))
            }

            status?.let {
                predicates.add(root.get<SupplierStatusEnum>("status").`in`(it))
            }

            names?.let {
                predicates.add(root.get<String>("name").`in`(it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun createDto(supplier: SupplierModel, full: Boolean = false): SupplierDto {
        val supplierDto = SupplierDto(
            id = supplier.id,
            name = supplier.name,
            status = supplier.status,
            score = supplier.score,
            orderMinValue = supplier.orderMinValue,
            exchange = supplier.exchange,
            orders = supplier.orders,
            ordersNotDelivered = supplier.ordersNotDelivered,
            ordersNotDeliveredPercentage = supplier.ordersNotDeliveredPercentage,
            totalValue = supplier.totalValue,
            valueReceived = supplier.valueReceived,
            valueReceivedPercentage = supplier.valueReceivedPercentage,
            averageValueReceived = supplier.averageValueReceived,
            minValueReceived = supplier.minValueReceived
        )

        if (full) {
            val supplierStores = supplierStoresService.findByParentId(supplier.id)
            supplierDto.stores = supplierStores
        }

        return supplierDto
    }

    fun findById(id: Long): SupplierModel {
        return supplierRepository.findById(id)
            .orElseThrow { throw ResourceNotFoundException("Supplier not Found") }
    }

    fun getAll(names: List<String>?): List<SupplierModel> {
        val spec = createCriteria(names = names)
        return supplierRepository.findAll(spec)
    }

    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        name: String?,
        exchange: Boolean?,
        status: List<SupplierStatusEnum>?,
        names: List<String>?
    ): Any {
        val spec = createCriteria(name, exchange, status, names)
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = supplierRepository.findAll(spec, pageable)

                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val suppliers = supplierRepository.findAll(spec, sortBy)
                suppliers.map { createDto(it) }
            }
        }
    }

    fun getById(id: Long, full: Boolean = false): SupplierDto {
        val supplier = findById(id)

        return createDto(supplier, full)
    }

    fun findByName(name: String): Optional<SupplierModel> {
        return supplierRepository.findByName(name)
    }

    @Transactional
    fun create(request: SupplierSchema) {
        val supplier = supplierRepository.saveAndFlush(
            SupplierModel(
                name = request.name,
                status = request.status,
                exchange = request.exchange,
                score = request.score,
                orderMinValue = request.orderMinValue,
                orders = request.orders,
                ordersNotDelivered = request.ordersNotDelivered,
                ordersNotDeliveredPercentage = request.ordersNotDeliveredPercentage,
                totalValue = request.totalValue,
                valueReceived = request.valueReceived,
                valueReceivedPercentage = request.valueReceivedPercentage,
                averageValueReceived = request.averageValueReceived,
                minValueReceived = request.minValueReceived
            )
        )
        supplierStoresService.createMultiple(supplier, request.stores)
    }

    @Transactional
    fun edit(id: Long, request: SupplierSchema) {
        val supplier = findById(id)

        val editedSupplier = supplierRepository.saveAndFlush(
            supplier.copy(
                name = request.name,
                status = request.status,
                exchange = request.exchange,
                score = request.score,
                orderMinValue = request.orderMinValue,
                orders = request.orders,
                ordersNotDelivered = request.ordersNotDelivered,
                ordersNotDeliveredPercentage = request.ordersNotDeliveredPercentage,
                totalValue = request.totalValue,
                valueReceived = request.valueReceived,
                valueReceivedPercentage = request.valueReceivedPercentage,
                averageValueReceived = request.averageValueReceived,
                minValueReceived = request.minValueReceived
            )
        )
        supplierStoresService.editOrCreateMultiple(editedSupplier, request.stores)
    }

    @Transactional
    fun editOrCreateMultiple(request: List<SupplierSchema>) {
        val suppliersNames = request.map { it.name }
        val suppliers = getAll(suppliersNames)

        if (suppliersNames.size != suppliers.size) throw IllegalArgumentException("One or more Suppliers not found")

        val suppliersMap = suppliers.associateBy { it.name }
        request.forEach { supp ->
            val foundSupplier = suppliersMap[supp.name]

            val result = supplierRepository.save(
                foundSupplier?.copy(
                    name = supp.name,
                    status = supp.status,
                    exchange = supp.exchange,
                    score = supp.score,
                    orderMinValue = supp.orderMinValue,
                    orders = supp.orders,
                    ordersNotDelivered = supp.ordersNotDelivered,
                    ordersNotDeliveredPercentage = supp.ordersNotDeliveredPercentage,
                    totalValue = supp.totalValue,
                    valueReceived = supp.valueReceived,
                    valueReceivedPercentage = supp.valueReceivedPercentage,
                    averageValueReceived = supp.averageValueReceived,
                    minValueReceived = supp.minValueReceived
                ) ?: SupplierModel(
                    name = supp.name,
                    status = supp.status,
                    exchange = supp.exchange,
                    score = supp.score,
                    orderMinValue = supp.orderMinValue,
                    orders = supp.orders,
                    ordersNotDelivered = supp.ordersNotDelivered,
                    ordersNotDeliveredPercentage = supp.ordersNotDeliveredPercentage,
                    totalValue = supp.totalValue,
                    valueReceived = supp.valueReceived,
                    valueReceivedPercentage = supp.valueReceivedPercentage,
                    averageValueReceived = supp.averageValueReceived,
                    minValueReceived = supp.minValueReceived
                )
            )
            supplierStoresService.editOrCreateMultiple(result, supp.stores)
        }
    }
}