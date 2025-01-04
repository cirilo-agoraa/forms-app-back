package agoraa.app.forms_back.service

import agoraa.app.forms_back.dto.supplier.SupplierDto
import agoraa.app.forms_back.enum.supplier.SupplierDtoOptionsEnum
import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.repository.SupplierRepository
import agoraa.app.forms_back.schema.supplier.SupplierCreateSchema
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
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

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

    fun createDto(fields: List<String>, supplierModel: SupplierModel): SupplierDto {
        val supplierDto = SupplierDto(
            id = supplierModel.id
        )

        val supplierModelProperties = SupplierModel::class.memberProperties.associateBy { it.name }
        val supplierDtoProperties = SupplierDto::class.memberProperties.associateBy { it.name }

        fields.forEach { field ->
            val modelProperty = supplierModelProperties[field]
            val dtoProperty = supplierDtoProperties[field]

            if (modelProperty != null && dtoProperty != null) {
                if (dtoProperty is KMutableProperty<*>) {
                    val value = modelProperty.get(supplierModel)
                    dtoProperty.setter.call(supplierDto, value)
                }
            }
        }

        return supplierDto
    }

    fun findById(id: Long): SupplierModel {
        return supplierRepository.findById(id)
            .orElseThrow { throw ResourceNotFoundException("Supplier not Found") }
    }

    fun getAll(
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        dtoOptions: SupplierDtoOptionsEnum,
        name: String?,
        status: List<SupplierStatusEnum>?
    ): Any {
        val spec = createCriteria(name, status)
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pagedResult = supplierRepository.findAll(spec, pageable)
                PageImpl(pagedResult.content.map { createDto(dtoOptions.fields, it) }, pageable, pagedResult.totalElements)
            }

            else -> {
                supplierRepository.findAll(spec, sortBy).map { createDto(dtoOptions.fields, it) }
            }
        }
    }

    fun getById(dtoOptions: SupplierDtoOptionsEnum, id: Long): SupplierDto {
        return createDto(dtoOptions.fields, findById(id))
    }

    fun findByName(name: String): SupplierModel {
        return supplierRepository.findByName(name)
            .orElseThrow { throw ResourceNotFoundException("Supplier not Found") }
    }

    @Transactional
    fun createMultiple(dtoOptions: SupplierDtoOptionsEnum, request: List<SupplierCreateSchema>): List<SupplierDto> {
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

        val newSuppliers = supplierRepository.saveAll(suppliers)
        return newSuppliers.map { createDto(dtoOptions.fields, it) }
    }

    @Transactional
    fun editOrCreateMultipleByName(dtoOptions: SupplierDtoOptionsEnum, request: List<SupplierCreateSchema>): List<SupplierDto> {
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
            } catch (e: ResourceNotFoundException) {
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
        val savedSuppliers = supplierRepository.saveAll(suppliers)
        return savedSuppliers.map { createDto(dtoOptions.fields, it) }
    }
}