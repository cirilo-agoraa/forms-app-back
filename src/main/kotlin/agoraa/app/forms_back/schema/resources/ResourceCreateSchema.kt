package agoraa.app.forms_back.schema.resources

import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.constraints.NotNull

data class ResourceCreateSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    @field:NotNull(message = "Products cannot be null")
    val products: List<ResourceProductsCreateSchema>
)
