package agoraa.app.forms_back.schema.resource_mips

import agoraa.app.forms_back.enum.StoresEnum
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size


data class ResourceMipCreateSchema(
    @field:NotNull(message = "Store cannot be null")
    @field:Pattern(regexp = "TRESMANN_SMJ|TRESMANN_VIX|TRESMANN_STT", message = "Invalid Store")
    val store: StoresEnum,

    @field:NotNull(message = "items is required")
    @field:Size(min = 1, message = "items must have at least 1 element")
    val items: List<ResourceMipProductsCreateSchema>
)
