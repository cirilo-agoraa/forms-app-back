package agoraa.app.forms_back.service

import agoraa.app.forms_back.enums.supplier.SupplierStatusEnum
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.repository.SupplierRepository
import agoraa.app.forms_back.schema.supplier.SupplierCreateSchema
import agoraa.app.forms_back.schema.supplier.SupplierEditMultipleSchema
import agoraa.app.forms_back.schema.supplier.SupplierEditSchema
import org.springdoc.api.OpenApiResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SupplierService(private val supplierRepository: SupplierRepository) {

    fun findAll(
        pagination: String,
        name: String,
        status: String,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

        val queryMap = mapOf(
            "name" to { supplierRepository.findByNameContaining(name, pageable) },
            "status" to { supplierRepository.findByStatus(SupplierStatusEnum.valueOf(status).name, pageable) }
        )
        return when {
            pagination.toBoolean() -> queryMap.entries.firstOrNull()?.value?.invoke()
                ?: supplierRepository.findAll(pageable)
            name.isNotEmpty() -> supplierRepository.findByNameContaining(name)
            status.isNotEmpty() -> supplierRepository.findByStatus(SupplierStatusEnum.valueOf(status).name)
            else -> supplierRepository.findAll()
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
    fun createBatch(request: List<SupplierCreateSchema>): List<SupplierModel> {
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

    fun create(request: SupplierCreateSchema): SupplierModel {
        val supplier = SupplierModel(
            name = request.name,
            status = SupplierStatusEnum.valueOf(request.status),
            orders = request.orders,
            ordersNotDelivered = request.ordersNotDelivered,
            ordersNotDeliveredPercentage = request.ordersNotDeliveredPercentage,
            totalValue = request.totalValue,
            valueReceived = request.valueReceived,
            valueReceivedPercentage = request.valueReceivedPercentage,
            averageValueReceived = request.averageValueReceived,
            minValueReceived = request.minValueReceived
        )
        return supplierRepository.save(supplier)
    }

    fun edit(id: Long, request: SupplierEditSchema): SupplierModel {
        val supplier = findById(id)
        val updatedSupplier = supplier.copy(
            name = request.name ?: supplier.name,
            status = request.status?.let { SupplierStatusEnum.valueOf(it) } ?: supplier.status,
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
        return supplierRepository.save(updatedSupplier)
    }

    @Transactional
    fun editMultipleByName(request: List<SupplierEditMultipleSchema>): List<SupplierModel> {
        val suppliers = request.map { supplier ->
            val supplierModel = findByName(supplier.name)
            supplierModel.copy(
                status = supplier.status?.let { SupplierStatusEnum.valueOf(it) } ?: supplierModel.status,
                orders = supplier.orders ?: supplierModel.orders,
                ordersNotDelivered = supplier.ordersNotDelivered ?: supplierModel.ordersNotDelivered,
                ordersNotDeliveredPercentage = supplier.ordersNotDeliveredPercentage
                    ?: supplierModel.ordersNotDeliveredPercentage,
                totalValue = supplier.totalValue ?: supplierModel.totalValue,
                valueReceived = supplier.valueReceived ?: supplierModel.valueReceived,
                valueReceivedPercentage = supplier.valueReceivedPercentage ?: supplierModel.valueReceivedPercentage,
                averageValueReceived = supplier.averageValueReceived ?: supplierModel.averageValueReceived,
                minValueReceived = supplier.minValueReceived ?: supplierModel.minValueReceived
            )
        }
        return supplierRepository.saveAll(suppliers)
    }
}