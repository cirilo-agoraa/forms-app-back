package agoraa.app.forms_back.resources.resources.dto.request

import agoraa.app.forms_back.resources.resource_products.dto.request.ResourceProductsRequest
import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.constraints.NotNull

data class ResourceRequest(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    @field:NotNull(message = "Products cannot be null")
    val products: List<ResourceProductsRequest>,

    val processed: Boolean = false,
    val orderNumber: Long? = null
)
