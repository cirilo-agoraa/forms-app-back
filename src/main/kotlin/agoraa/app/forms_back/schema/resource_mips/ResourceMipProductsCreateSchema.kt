package agoraa.app.forms_back.schema.resource_mips

import jakarta.validation.constraints.NotNull

data class ResourceMipProductsCreateSchema(
    @field:NotNull(message = "quantity is required")
    val quantity: Int,

    @field:NotNull(message = "product is required")
    val productId: Long
)