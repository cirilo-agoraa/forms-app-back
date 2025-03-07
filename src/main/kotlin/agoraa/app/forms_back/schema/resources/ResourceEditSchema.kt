package agoraa.app.forms_back.schema.resources

import agoraa.app.forms_back.enum.StoresEnum

data class ResourceEditSchema(
    val store: StoresEnum? = null,
    val products: List<ResourceProductsEditSchema>? = null,
    val processed: Boolean? = null,
    val orderNumber: Long? = null
)
