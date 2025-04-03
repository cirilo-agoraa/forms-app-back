package agoraa.app.forms_back.schema.resource_mips

import agoraa.app.forms_back.shared.enums.StoresEnum

data class ResourceMipEditSchema(
    val store: agoraa.app.forms_back.shared.enums.StoresEnum? = null,
    val processed: Boolean? = null,
    val items: List<ResourceMipProductsEditSchema>? = null,
)