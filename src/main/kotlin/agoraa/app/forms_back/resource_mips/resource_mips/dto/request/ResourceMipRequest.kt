package agoraa.app.forms_back.resource_mips.resource_mips.dto.request

import agoraa.app.forms_back.resource_mips.resource_mip_products.dto.request.ResourceMipProductsRequest
import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


data class ResourceMipRequest(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    @field:NotNull(message = "items is required")
    @field:Size(min = 1, message = "items must have at least 1 element")
    val products: List<ResourceMipProductsRequest>,

    val processed: Boolean = false
)
