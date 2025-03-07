package agoraa.app.forms_back.schema.resources

import jakarta.validation.constraints.NotNull

data class ResourceProductsEditSchema(
    @field:NotNull(message = "Product Id cannot be null")
    val productId: Long,
    val quantity: Int? = null,
    val qttSent: Int? = null,
    val qttReceived: Int? = null,
)
