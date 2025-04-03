package agoraa.app.forms_back.dto.resource_products

import agoraa.app.forms_back.products.products.dto.response.ProductResponse

data class ResourceProductsDto(
    val id: Long = 0,
    val product: ProductResponse,
    val quantity: Int,
    val qttSent: Int? = null,
    val qttReceived: Int? = null,
)
