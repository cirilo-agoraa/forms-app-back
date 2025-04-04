package agoraa.app.forms_back.resources.resource_products.dto.response

import agoraa.app.forms_back.products.products.model.ProductModel
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ResourceProductsResponse(
    val id: Long = 0,
    val product: ProductModel,
    val quantity: Int,
    val qttSent: Int? = null,
    val qttReceived: Int? = null,
)
