package agoraa.app.forms_back.service.suppliers

import agoraa.app.forms_back.dto.suppliers.SupplierStoresDto
import agoraa.app.forms_back.model.suppliers.SupplierModel
import agoraa.app.forms_back.model.suppliers.SupplierStoresModel
import agoraa.app.forms_back.repository.suppliers.SupplierStoresRepository
import agoraa.app.forms_back.schema.supplier.SupplierStoresCreateSchema
import agoraa.app.forms_back.schema.supplier.SupplierStoresEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class SupplierStoresService(private val supplierStoresRepository: SupplierStoresRepository) {
    private fun editMultiple(
        supplierStores: List<SupplierStoresModel>,
        stores: List<SupplierStoresEditSchema>
    ) {
        val editedSupplierStores = supplierStores.map { srs ->
            val updatedSs = stores.find { it.store == srs.store }
            srs.copy(
                orderMeanDeliveryTime = updatedSs?.orderMeanDeliveryTime?.get() ?: srs.orderMeanDeliveryTime,
                orderTerm = updatedSs?.orderTerm?.get() ?: srs.orderTerm,
                stock = updatedSs?.stock?.get() ?: srs.stock,
                frequency = updatedSs?.frequency?.get() ?: srs.frequency,
                openOrder = updatedSs?.openOrder?.get() ?: srs.openOrder,
                orderDay = updatedSs?.orderDay?.get() ?: srs.orderDay,
                nextOrder = updatedSs?.nextOrder?.get() ?: srs.nextOrder,
                openOrderExpectedDelivery = updatedSs?.openOrderExpectedDelivery?.get() ?: srs.openOrderExpectedDelivery,
                openOrderRealDelivery = updatedSs?.openOrderExpectedDelivery?.get() ?: srs.openOrderRealDelivery,
                exchangeStock = updatedSs?.exchangeStock?.get() ?: srs.exchangeStock,
            )
        }

        supplierStoresRepository.saveAllAndFlush(editedSupplierStores)
    }

    private fun createCriteria(
        supplierId: Long? = null,
    ): Specification<SupplierStoresModel> {
        return Specification { root: Root<SupplierStoresModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            supplierId?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<SupplierModel>("supplier").get<Long>("id"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(supplierStores: SupplierStoresModel): SupplierStoresDto {
        return SupplierStoresDto(
            id = supplierStores.id,
            store = supplierStores.store,
            openOrder = supplierStores.openOrder,
            orderDay = supplierStores.orderDay,
            stock = supplierStores.stock,
            exchangeStock = supplierStores.exchangeStock,
            orderMeanDeliveryTime = supplierStores.orderMeanDeliveryTime,
            frequency = supplierStores.frequency,
            orderTerm = supplierStores.orderTerm,
            nextOrder = supplierStores.nextOrder,
            openOrderExpectedDelivery = supplierStores.openOrderExpectedDelivery,
            openOrderRealDelivery = supplierStores.openOrderRealDelivery,
        )
    }

    fun findByParentId(
        supplierId: Long,
    ): List<SupplierStoresDto> {
        val spec = createCriteria(supplierId)

        return supplierStoresRepository.findAll(spec).map { createDto(it) }
    }

    fun create(supplier: SupplierModel, stores: List<SupplierStoresCreateSchema>) {
        val supplierStores = stores.map { p ->
            SupplierStoresModel(
                supplier = supplier,
                store = p.store,
                orderDay = p.orderDay,
                frequency = p.frequency,
                stock = p.stock,
                exchangeStock = p.exchangeStock,
                openOrder = p.openOrder,
                orderTerm = p.orderTerm,
                orderMeanDeliveryTime = p.orderMeanDeliveryTime,
                nextOrder = p.nextOrder,
                openOrderRealDelivery = p.openOrderRealDelivery,
                openOrderExpectedDelivery = p.openOrderExpectedDelivery,
            )
        }
        supplierStoresRepository.saveAll(supplierStores)
    }

    fun edit(supplier: SupplierModel, stores: List<SupplierStoresEditSchema>) {
        val spec = createCriteria(supplier.id)
        val supplierStores = supplierStoresRepository.findAll(spec)
        val currentSupplierStores = supplierStores.map { it.store }.toSet()
        val editSpsSet = stores.map { it.store }.toSet()

        val toAdd = stores.filter { it.store !in currentSupplierStores }
        val newSupplierStores = toAdd.map { p ->
            SupplierStoresModel(
                supplier = supplier,
                store = p.store,
                orderDay = p.orderDay?.get(),
                frequency = p.frequency?.get() ?: throw IllegalArgumentException("frequency is required"),
                stock = p.stock?.get() ?: throw IllegalArgumentException("stock is required"),
                exchangeStock = p.exchangeStock?.get() ?: throw IllegalArgumentException("exchangeStock is required"),
                openOrder = p.openOrder?.get() ?: throw IllegalArgumentException("openOrder is required"),
                orderTerm = p.orderTerm?.get() ?: throw IllegalArgumentException("orderTerm is required"),
                orderMeanDeliveryTime = p.orderMeanDeliveryTime?.get() ?: throw IllegalArgumentException("orderMeanDeliveryTime is required"),
                nextOrder = p.nextOrder?.get(),
                openOrderExpectedDelivery = p.openOrderExpectedDelivery?.get(),
                openOrderRealDelivery = p.openOrderRealDelivery?.get(),
            )
        }
        supplierStoresRepository.saveAll(newSupplierStores)

        val toEdit = supplierStores.filter { it.store in editSpsSet }
        editMultiple(toEdit, stores)
    }
}