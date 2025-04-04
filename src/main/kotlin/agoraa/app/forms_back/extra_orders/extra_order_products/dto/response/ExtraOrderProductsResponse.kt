package agoraa.app.forms_back.extra_orders.extra_order_products.dto.response

import agoraa.app.forms_back.products.products.model.ProductModel

data class ExtraOrderProductsResponse(
    val id: Long,
    val product: ProductModel,
    val price: Double,
    val quantity: Int
)