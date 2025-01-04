package agoraa.app.forms_back.schema.extra_order_product

import agoraa.app.forms_back.model.ProductModel
import jakarta.validation.constraints.NotNull

data class ExtraOrderProductCreateSchema(
    @field:NotNull
    val productId: Long,

    @field:NotNull
    val price: Double,

    @field:NotNull
    val quantity: Int,
)
