package agoraa.app.forms_back.suppliers.supplier_stores.service

import agoraa.app.forms_back.suppliers.supplier_stores.dto.request.SupplierStoresRequest
import agoraa.app.forms_back.suppliers.supplier_stores.dto.response.SupplierStoresResponse
import agoraa.app.forms_back.suppliers.supplier_stores.model.SupplierStoresModel
import agoraa.app.forms_back.suppliers.supplier_stores.repository.SupplierStoresRepository
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class SupplierStoresService(private val supplierStoresRepository: SupplierStoresRepository) {
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

    fun createDto(supplierStores: SupplierStoresModel): SupplierStoresResponse {
        return SupplierStoresResponse(
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
            sellerName = supplierStores.sellerName,
            sellerEmail = supplierStores.sellerEmail,
            sellerPhone = supplierStores.sellerPhone
        )
    }

    fun findByParentId(
        supplierId: Long,
    ): List<SupplierStoresResponse> {
        val spec = createCriteria(supplierId)

        return supplierStoresRepository.findAll(spec).map { createDto(it) }
    }

    fun createMultiple(supplier: SupplierModel, stores: List<SupplierStoresRequest>) {
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

    fun editOrCreateMultiple(supplier: SupplierModel, stores: List<SupplierStoresRequest>) {
        val spec = createCriteria(supplier.id)
        val supplierStores = supplierStoresRepository.findAll(spec)
        val currentSupplierStores = supplierStores.map { it.store }.toSet()
        val editSpsSet = stores.map { it.store }.toSet()

        val toAdd = stores.filter { it.store !in currentSupplierStores }
        createMultiple(supplier, toAdd)

        val toEdit = supplierStores.filter { it.store in editSpsSet }
        val editedSupplierStores = toEdit.map { srs ->
            val updatedSs = stores.find { it.store == srs.store }!!
            srs.copy(
                orderMeanDeliveryTime = updatedSs.orderMeanDeliveryTime,
                orderTerm = updatedSs.orderTerm,
                stock = updatedSs.stock,
                frequency = updatedSs.frequency,
                openOrder = updatedSs.openOrder,
                exchangeStock = updatedSs.exchangeStock,
                orderDay = updatedSs.orderDay,
                nextOrder = updatedSs.nextOrder,
                openOrderExpectedDelivery = updatedSs.openOrderExpectedDelivery,
                openOrderRealDelivery = updatedSs.openOrderRealDelivery,
            )
        }

        supplierStoresRepository.saveAllAndFlush(editedSupplierStores)
    }
}