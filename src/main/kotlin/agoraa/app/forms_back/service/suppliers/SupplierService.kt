package agoraa.app.forms_back.service.suppliers

import agoraa.app.forms_back.dto.suppliers.SupplierDto
import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.suppliers.SupplierModel
import agoraa.app.forms_back.repository.suppliers.SupplierRepository
import agoraa.app.forms_back.schema.supplier.SupplierCreateSchema
import agoraa.app.forms_back.schema.supplier.SupplierEditOrCreateSchema
import agoraa.app.forms_back.schema.supplier.SupplierEditSchema
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
        name: String?,
        exchange: Boolean?,
        status: List<SupplierStatusEnum>?
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

    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        name: String?,
        exchange: Boolean?,
        status: List<SupplierStatusEnum>?
    ): Any {
        val spec = createCriteria(name, exchange, status)
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
    fun create(request: SupplierCreateSchema) {
        val supplier = SupplierModel(
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

        val createdSupplier = supplierRepository.saveAndFlush(supplier)

        supplierStoresService.create(createdSupplier, request.stores)
    }

    @Transactional
    fun edit(id: Long, request: SupplierEditSchema) {
        val supplier = findById(id)

        val editedSupplier = supplierRepository.saveAndFlush(
            supplier.copy(
                name = request.name ?: supplier.name,
                status = request.status ?: supplier.status,
                exchange = request.exchange ?: supplier.exchange,
                score = request.score ?: supplier.score,
                orderMinValue = request.orderMinValue ?: supplier.orderMinValue,
                orders = request.orders ?: supplier.orders,
                ordersNotDelivered = request.ordersNotDelivered ?: supplier.ordersNotDelivered,
                ordersNotDeliveredPercentage = request.ordersNotDeliveredPercentage
                    ?: supplier.ordersNotDeliveredPercentage,
                totalValue = request.totalValue ?: supplier.totalValue,
                valueReceived = request.valueReceived ?: supplier.valueReceived,
                valueReceivedPercentage = request.valueReceivedPercentage ?: supplier.valueReceivedPercentage,
                averageValueReceived = request.averageValueReceived ?: supplier.averageValueReceived,
                minValueReceived = request.minValueReceived ?: supplier.minValueReceived
            )
        )

        request.stores?.let { supplierStoresService.edit(editedSupplier, it) }
    }

    @Transactional
    fun editOrCreateMultiple(request: List<SupplierEditOrCreateSchema>) {
        request.forEach { supp ->
            val supplier = findByName(supp.name).orElse(null)

            if (supplier != null) {
                val editedSupplier = supplierRepository.saveAndFlush(
                    supplier.copy(
                        status = supp.status ?: supplier.status,
                        exchange = supp.exchange ?: supplier.exchange,
                        score = supp.score ?: supplier.score,
                        orderMinValue = supp.orderMinValue ?: supplier.orderMinValue,
                        orders = supp.orders ?: supplier.orders,
                        ordersNotDelivered = supp.ordersNotDelivered ?: supplier.ordersNotDelivered,
                        ordersNotDeliveredPercentage = supp.ordersNotDeliveredPercentage
                            ?: supplier.ordersNotDeliveredPercentage,
                        totalValue = supp.totalValue ?: supplier.totalValue,
                        valueReceived = supp.valueReceived ?: supplier.valueReceived,
                        valueReceivedPercentage = supp.valueReceivedPercentage ?: supplier.valueReceivedPercentage,
                        averageValueReceived = supp.averageValueReceived ?: supplier.averageValueReceived,
                        minValueReceived = supp.minValueReceived ?: supplier.minValueReceived
                    )
                )
                supp.stores?.let { supplierStoresService.edit(editedSupplier, it) }
            }
            else {
                val newSupplier = supplierRepository.saveAndFlush(
                    SupplierModel(
                        name = supp.name,
                        status = supp.status ?: throw IllegalArgumentException("status is required"),
                        exchange = supp.exchange ?: false,
                        score = supp.score ?: 0,
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
                supp.stores?.let { supplierStoresService.edit(newSupplier, it) }
            }
        }
    }
}