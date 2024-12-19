package agoraa.app.forms_back.service

import agoraa.app.forms_back.enums.supplier.SupplierStatusEnum
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.repository.SupplierRepository
import agoraa.app.forms_back.schema.supplier.SupplierCreateSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupplierService(private val supplierRepository: SupplierRepository) {

    private fun createCriteria(name: String?, status: List<SupplierStatusEnum>?): Specification<SupplierModel> {
        return Specification { root: Root<SupplierModel>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            name?.let {
                predicates.add(criteriaBuilder.like(root.get("name"), "%$it%"))
            }

            status?.let {
                predicates.add(root.get<SupplierStatusEnum>("status").`in`(it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun findAll(
        pagination: Boolean,
        name: String?,
        status: List<SupplierStatusEnum>?,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val spec = createCriteria(name, status)

        if(pagination) {
            val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
            val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
            return supplierRepository.findAll(spec, pageable)
        } else {
            return supplierRepository.findAll(spec)
        }
    }

    fun findById(id: Long): SupplierModel {
        return supplierRepository.findById(id)
            .orElseThrow { throw ResourceNotFoundException("Supplier not Found") }
    }

    fun findByName(name: String): SupplierModel {
        return supplierRepository.findByName(name)
            .orElseThrow { throw ResourceNotFoundException("Supplier not Found") }
    }

    @Transactional
    fun createMultiple(request: List<SupplierCreateSchema>): List<SupplierModel> {
        val suppliers = request
            .map { supplier ->
                SupplierModel(
                    name = supplier.name,
                    status = SupplierStatusEnum.valueOf(supplier.status),
                    orders = supplier.orders,
                    ordersNotDelivered = supplier.ordersNotDelivered,
                    ordersNotDeliveredPercentage = supplier.ordersNotDeliveredPercentage,
                    totalValue = supplier.totalValue,
                    valueReceived = supplier.valueReceived,
                    valueReceivedPercentage = supplier.valueReceivedPercentage,
                    averageValueReceived = supplier.averageValueReceived,
                    minValueReceived = supplier.minValueReceived
                )
            }
        return supplierRepository.saveAll(suppliers)
    }

    @Transactional
    fun editOrCreateMultipleByName(request: List<SupplierCreateSchema>): List<SupplierModel> {
        val suppliers = request.map { supplier ->
            try {
                val existingSupplier = findByName(supplier.name)
                existingSupplier.copy(
                    status = SupplierStatusEnum.valueOf(supplier.status),
                    orders = supplier.orders,
                    ordersNotDelivered = supplier.ordersNotDelivered,
                    ordersNotDeliveredPercentage = supplier.ordersNotDeliveredPercentage,
                    totalValue = supplier.totalValue,
                    valueReceived = supplier.valueReceived,
                    valueReceivedPercentage = supplier.valueReceivedPercentage,
                    averageValueReceived = supplier.averageValueReceived,
                    minValueReceived = supplier.minValueReceived
                )
            } catch(e: ResourceNotFoundException) {
                SupplierModel(
                    name = supplier.name,
                    status = SupplierStatusEnum.valueOf(supplier.status),
                    orders = supplier.orders,
                    ordersNotDelivered = supplier.ordersNotDelivered,
                    ordersNotDeliveredPercentage = supplier.ordersNotDeliveredPercentage,
                    totalValue = supplier.totalValue,
                    valueReceived = supplier.valueReceived,
                    valueReceivedPercentage = supplier.valueReceivedPercentage,
                    averageValueReceived = supplier.averageValueReceived,
                    minValueReceived = supplier.minValueReceived
                )
            }
        }
        return supplierRepository.saveAll(suppliers)
    }
}