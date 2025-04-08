package agoraa.app.forms_back.resources.resource_products.service

import agoraa.app.forms_back.resources.resource_products.dto.request.ResourceProductsPatchRequest
import agoraa.app.forms_back.resources.resource_products.dto.request.ResourceProductsRequest
import agoraa.app.forms_back.resources.resource_products.dto.response.ResourceProductsResponse
import agoraa.app.forms_back.resources.resource_products.model.ResourceProductsModel
import agoraa.app.forms_back.resources.resource_products.repository.ResourceProductsRepository
import agoraa.app.forms_back.resources.resources.model.ResourceModel
import org.springframework.stereotype.Service

@Service
class ResourceProductsService(
    private val resourceProductsRepository: ResourceProductsRepository
) {
    private fun create(
        resource: ResourceModel,
        products: List<ResourceProductsRequest>
    ) {
        val resourceProducts = products.map { p ->
            ResourceProductsModel(
                resource = resource,
                product = p.product,
                quantity = p.quantity,
                qttReceived = p.qttReceived,
                qttSent = p.qttSent
            )
        }
        resourceProductsRepository.saveAll(resourceProducts)
    }

    private fun edit(products: List<ResourceProductsModel>, request: List<ResourceProductsRequest>) {
        val productsMap = request.associateBy { it.product.code }
        val resourceProducts = products.map { p ->
            val requestProducts = productsMap[p.product.code]!!

            p.copy(
                product = requestProducts.product,
                quantity = requestProducts.quantity,
                qttReceived = requestProducts.qttReceived,
                qttSent = requestProducts.qttSent
            )
        }
        resourceProductsRepository.saveAll(resourceProducts)
    }

    private fun patch(products: List<ResourceProductsModel>, request: List<ResourceProductsPatchRequest>) {
        val productsMap = request.associateBy { it.productId }

        val resourceProducts = products.map { p ->
            val requestProducts = productsMap[p.product.id]!!

            p.copy(
                qttReceived = requestProducts.qttReceived,
                qttSent = requestProducts.qttSent
            )
        }
        resourceProductsRepository.saveAll(resourceProducts)
    }

    fun findByParentId(
        resourceId: Long,
    ): List<ResourceProductsResponse> =
        resourceProductsRepository.findByResourceId(resourceId).map { createDto(it) }

    fun createDto(resourceProducts: ResourceProductsModel): ResourceProductsResponse {
        return ResourceProductsResponse(
            id = resourceProducts.id,
            product = resourceProducts.product,
            quantity = resourceProducts.quantity,
            qttSent = resourceProducts.qttSent,
            qttReceived = resourceProducts.qttReceived
        )
    }

    fun editOrCreateOrDelete(
        resource: ResourceModel,
        products: List<ResourceProductsRequest>
    ) {
        val resourceProducts = resourceProductsRepository.findByResourceId(resource.id)
        val currentProductsSet = resourceProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(resource, toAdd)

        val toDelete = resourceProducts.filter { it.product !in newProductsSet }
        resourceProductsRepository.deleteAll(toDelete)

        val toEdit = resourceProducts.filter { it.product in newProductsSet }
        edit(toEdit, products)
    }

    fun patchProducts(
        resource: ResourceModel,
        products: List<ResourceProductsPatchRequest>
    ) {
        val resourceProducts = resourceProductsRepository.findByResourceId(resource.id)
        val newProductsSet = products.map { it.productId }.toSet()

        val toPatch = resourceProducts.filter { it.product.id in newProductsSet }
        patch(toPatch, products)
    }
}