package agoraa.app.forms_back.store_audits.store_audit_products.service

import agoraa.app.forms_back.store_audits.store_audit_products.dto.request.StoreAuditProductsRequest
import agoraa.app.forms_back.store_audits.store_audit_products.dto.response.StoreAuditProductsResponse
import agoraa.app.forms_back.store_audits.store_audit_products.model.StoreAuditProductsModel
import agoraa.app.forms_back.store_audits.store_audit_products.repository.StoreAuditProductsRepository
import agoraa.app.forms_back.store_audits.store_audits.model.StoreAuditModel
import org.springframework.stereotype.Service

@Service
class StoreAuditProductsService(private val storeAuditProductsRepository: StoreAuditProductsRepository) {
    private fun create(
        storeAudit: StoreAuditModel,
        products: List<StoreAuditProductsRequest>
    ) {
        val storeAuditProducts = products.map { p ->
            StoreAuditProductsModel(
                storeAudit = storeAudit,
                product = p.product,
                inStore = p.inStore
            )
        }
        storeAuditProductsRepository.saveAll(storeAuditProducts)
    }

    private fun edit(products: List<StoreAuditProductsModel>, request: List<StoreAuditProductsRequest>) {
        val productsMap = request.associateBy { it.product.code }
        val storeAuditProducts = products.map { p ->
            val requestProduct = productsMap[p.product.code]!!
            p.copy(
                product = requestProduct.product,
                inStore = requestProduct.inStore
            )
        }
        storeAuditProductsRepository.saveAll(storeAuditProducts)
    }

    fun createDto(storeAuditProducts: StoreAuditProductsModel): StoreAuditProductsResponse {
        return StoreAuditProductsResponse(
            id = storeAuditProducts.id,
            product = storeAuditProducts.product,
            inStore = storeAuditProducts.inStore
        )
    }

    fun findByParentId(storeAuditId: Long): List<StoreAuditProductsModel> =
        storeAuditProductsRepository.findByStoreAuditId(storeAuditId)

    fun editOrCreateOrDelete(
        storeAudit: StoreAuditModel,
        products: List<StoreAuditProductsRequest>
    ) {
        val storeAuditProducts = findByParentId(storeAudit.id)
        val currentProductsSet = storeAuditProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(storeAudit, toAdd)

        val toDelete = storeAuditProducts.filter { it.product !in newProductsSet }
        storeAuditProductsRepository.deleteAll(toDelete)

        val toEdit = storeAuditProducts.filter { it.product in newProductsSet }
        edit(toEdit, products)
    }
}