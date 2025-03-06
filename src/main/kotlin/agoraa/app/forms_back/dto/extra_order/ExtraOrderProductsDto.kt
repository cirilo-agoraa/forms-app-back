package agoraa.app.forms_back.dto.extra_order

import agoraa.app.forms_back.model.products.ProductModel

data class ExtraOrderProductsDto(
    val id: Long,
    val product: ProductModel,
    val price: Double,
    val quantity: Int
)