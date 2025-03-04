package agoraa.app.forms_back.schema.resource_mips

data class ResourceMipEditSchema(
    val processed: Boolean? = null,
    val items: List<ResourceMipItemsEditSchema>? = null,
)