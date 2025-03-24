package agoraa.app.forms_back.schema.resource_mips

import agoraa.app.forms_back.enums.StoresEnum
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


data class ResourceMipCreateSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    @field:NotNull(message = "items is required")
    @field:Size(min = 1, message = "items must have at least 1 element")
    val products: List<ResourceMipProductsCreateSchema>
)
