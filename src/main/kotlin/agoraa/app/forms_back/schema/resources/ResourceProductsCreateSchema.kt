package agoraa.app.forms_back.schema.resources

import jakarta.validation.constraints.NotNull

data class ResourceProductsCreateSchema(
    @field:NotNull(message = "product is required")
    val productId: Long,

    @field:NotNull(message = "quantity is required")
    val quantity: Int,
)
