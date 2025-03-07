package agoraa.app.forms_back.schema.resources

import agoraa.app.forms_back.enum.StoresEnum
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class ResourceCreateSchema(
    @field:NotNull(message = "Store cannot be null")
    @field:Pattern(regexp = "TRESMANN_SMJ|TRESMANN_VIX|TRESMANN_STT", message = "Invalid Store")
    val store: StoresEnum,
    @field:NotNull(message = "Products cannot be null")
    val products: List<ResourceProductsCreateSchema>
)
