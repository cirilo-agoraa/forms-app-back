package agoraa.app.forms_back.schema.resource_mips

import agoraa.app.forms_back.enum.MipsCategoriesEnum
import jakarta.validation.constraints.NotNull

data class ResourceMipItemsCreateSchema(
    @field:NotNull(message = "quantity is required")
    val quantity: Int,

    @field:NotNull(message = "category is required")
    val category: MipsCategoriesEnum
)