package agoraa.app.forms_back.schema.resource_mips

import agoraa.app.forms_back.enum.StoresEnum

data class ResourceMipEditSchema(
    val store: StoresEnum? = null,
    val processed: Boolean? = null,
    val items: List<ResourceMipProductsEditSchema>? = null,
)