package agoraa.app.forms_back.extra_orders.extra_order_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel
import jakarta.validation.constraints.NotNull

data class ExtraOrderProductRequest(
    @field:NotNull(message = "Product cannot be null")
    val product: ProductModel,

    @field:NotNull(message = "Price cannot be null")
    val price: Double,

    @field:NotNull(message = "Quantity cannot be null")
    val quantity: Int,
)
