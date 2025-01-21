package agoraa.app.forms_back.dto.resource_products

import agoraa.app.forms_back.dto.product.ProductDto

data class ResourceProductsDto(
    val id: Long = 0,
    val product: ProductDto,
    val quantity: Int
)
