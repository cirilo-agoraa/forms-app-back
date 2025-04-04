package agoraa.app.forms_back.resources.resource_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel
import jakarta.validation.constraints.NotNull

data class ResourceProductsRequest(
    @field:NotNull(message = "product is required")
    val product: ProductModel,

    @field:NotNull(message = "quantity is required")
    val quantity: Int,

    val qttSent: Int? = null,
    val qttReceived: Int? = null,
)
