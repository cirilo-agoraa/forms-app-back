package agoraa.app.forms_back.schema.resource_mips

import agoraa.app.forms_back.enum.MipsCategoriesEnum

data class ResourceMipItemsEditSchema(
    val category: MipsCategoriesEnum? = null,
    val quantity: Int? = null,
)