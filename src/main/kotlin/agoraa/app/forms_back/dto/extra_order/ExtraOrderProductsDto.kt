package agoraa.app.forms_back.dto.extra_order

import agoraa.app.forms_back.products.products.model.ProductModel

data class ExtraOrderProductsDto(
    val id: Long,
    val product: ProductModel,
    val price: Double,
    val quantity: Int
)