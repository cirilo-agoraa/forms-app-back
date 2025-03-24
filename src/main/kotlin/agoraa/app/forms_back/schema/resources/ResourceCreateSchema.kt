package agoraa.app.forms_back.schema.resources

import agoraa.app.forms_back.enums.StoresEnum
import jakarta.validation.constraints.NotNull

data class ResourceCreateSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,
    @field:NotNull(message = "Products cannot be null")
    val products: List<ResourceProductsCreateSchema>
)
