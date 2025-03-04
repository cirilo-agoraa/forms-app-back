package agoraa.app.forms_back.schema.resource_mips

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


data class ResourceMipCreateSchema(
    @field:NotNull(message = "items is required")
    @field:Size(min = 1, message = "items must have at least 1 element")
    val items: List<ResourceMipItemsCreateSchema>
)
