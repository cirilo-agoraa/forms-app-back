package agoraa.app.forms_back.resource_mips.resource_mip_products.service

import agoraa.app.forms_back.resource_mips.resource_mip_products.dto.request.ResourceMipProductsRequest
import agoraa.app.forms_back.resource_mips.resource_mip_products.dto.response.ResourceMipProductsResponse
import agoraa.app.forms_back.resource_mips.resource_mip_products.model.ResourceMipProductsModel
import agoraa.app.forms_back.resource_mips.resource_mip_products.repository.ResourceMipProductsRepository
import agoraa.app.forms_back.resource_mips.resource_mips.model.ResourceMipModel
import org.springframework.stereotype.Service

@Service
class ResourceMipProductsService(
    private val resourceMipProductsRepository: ResourceMipProductsRepository,
) {
    private fun create(
        resourceMip: ResourceMipModel,
        products: List<ResourceMipProductsRequest>
    ) {
        val resourceMipsProducts = products.map { p ->
            ResourceMipProductsModel(
                resourceMip = resourceMip,
                product = p.product,
                quantity = p.quantity
            )
        }
        resourceMipProductsRepository.saveAll(resourceMipsProducts)
    }

    private fun edit(products: List<ResourceMipProductsModel>, request: List<ResourceMipProductsRequest>) {
        val productsMap = request.associateBy { it.product.code }
        val resourceMipsProducts = products.map { p ->
            val requestProduct = productsMap[p.product.code]!!
            p.copy(
                product = requestProduct.product,
                quantity = requestProduct.quantity
            )
        }
        resourceMipProductsRepository.saveAll(resourceMipsProducts)
    }

    fun findByParentId(
        resourceMipId: Long,
    ): List<ResourceMipProductsResponse> =
        resourceMipProductsRepository.findByResourceMipId(resourceMipId).map { createDto(it) }

    fun createDto(resourceMipProducts: ResourceMipProductsModel): ResourceMipProductsResponse {
        return ResourceMipProductsResponse(
            id = resourceMipProducts.id,
            product = resourceMipProducts.product,
            quantity = resourceMipProducts.quantity
        )
    }

    fun editOrCreateOrDelete(
        extraQuotation: ResourceMipModel,
        products: List<ResourceMipProductsRequest>
    ) {
        val resourceMipProducts = resourceMipProductsRepository.findByResourceMipId(extraQuotation.id)
        val currentProductsSet = resourceMipProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(extraQuotation, toAdd)

        val toDelete = resourceMipProducts.filter { it.product !in newProductsSet }
        resourceMipProductsRepository.deleteAll(toDelete)

        val toEdit = resourceMipProducts.filter { it.product in newProductsSet }
        edit(toEdit, products)
    }
}