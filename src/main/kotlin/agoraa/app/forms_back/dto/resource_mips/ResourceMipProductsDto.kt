package agoraa.app.forms_back.dto.resource_mips

import agoraa.app.forms_back.dto.product.ProductDto

data class ResourceMipProductsDto(
    val id: Long,
    val product: ProductDto,
    val quantity: Int,
)
