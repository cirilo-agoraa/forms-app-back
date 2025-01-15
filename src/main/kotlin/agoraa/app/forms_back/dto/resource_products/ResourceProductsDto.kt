package agoraa.app.forms_back.dto.resource_products

import agoraa.app.forms_back.model.ProductModel

data class ResourceProductsDto(
    val id: Long = 0,
    val product: ProductModel,
    val quantity: Int
)
