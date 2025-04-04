package agoraa.app.forms_back.resource_mips.resource_mip_products.dto.response

import agoraa.app.forms_back.products.products.model.ProductModel

data class ResourceMipProductsResponse(
    val id: Long,
    val product: ProductModel,
    val quantity: Int,
)
