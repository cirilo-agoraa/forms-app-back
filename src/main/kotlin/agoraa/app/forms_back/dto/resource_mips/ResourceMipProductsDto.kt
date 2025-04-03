package agoraa.app.forms_back.dto.resource_mips

import agoraa.app.forms_back.products.products.dto.response.ProductResponse

data class ResourceMipProductsDto(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int,
)
