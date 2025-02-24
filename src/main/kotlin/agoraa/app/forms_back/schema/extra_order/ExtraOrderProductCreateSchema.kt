package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.model.ProductModel
import jakarta.validation.constraints.NotNull

data class ExtraOrderProductCreateSchema(
    @field:NotNull(message = "Product cannot be null")
    val product: ProductModel,

    @field:NotNull(message = "Price cannot be null")
    val price: Double,

    @field:NotNull(message = "Quantity cannot be null")
    val quantity: Int,
)
