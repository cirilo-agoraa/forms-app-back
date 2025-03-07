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
                orderMeanDeliveryTime = updatedSs?.orderMeanDeliveryTime ?: srs.orderMeanDeliveryTime,
                orderTerm = updatedSs?.orderTerm ?: srs.orderTerm,
                stock = updatedSs?.stock ?: srs.stock,
                frequency = updatedSs?.frequency ?: srs.frequency,
                openOrder = updatedSs?.openOrder ?: srs.openOrder,
                orderDay = updatedSs?.orderDay ?: srs.orderDay,
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
                orderDay = p.orderDay ?: throw IllegalArgumentException("orderDay is required"),
                frequency = p.frequency ?: throw IllegalArgumentException("frequency is required"),
                stock = p.stock ?: throw IllegalArgumentException("stock is required"),
                exchangeStock = p.exchangeStock ?: throw IllegalArgumentException("exchangeStock is required"),
                openOrder = p.openOrder ?: throw IllegalArgumentException("openOrder is required"),
                orderTerm = p.orderTerm ?: throw IllegalArgumentException("orderTerm is required"),
                orderMeanDeliveryTime = p.orderMeanDeliveryTime ?: throw IllegalArgumentException("orderMeanDeliveryTime is required"),
            )
        }
        supplierStoresRepository.saveAll(newSupplierStores)

        val toEdit = supplierStores.filter { it.store in editSpsSet }
        editMultiple(toEdit, stores)
    }
}