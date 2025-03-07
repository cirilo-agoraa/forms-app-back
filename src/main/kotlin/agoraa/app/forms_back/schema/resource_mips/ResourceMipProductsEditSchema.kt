package agoraa.app.forms_back.schema.resource_mips

import jakarta.validation.constraints.NotNull

data class ResourceMipProductsEditSchema(
    @field:NotNull(message = "Product Id cannot be null")
    val productId: Long,
    val quantity: Int? = null,
)