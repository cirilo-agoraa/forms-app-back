package agoraa.app.forms_back.schema.resources

import agoraa.app.forms_back.shared.enums.StoresEnum

data class ResourceEditSchema(
    val store: agoraa.app.forms_back.shared.enums.StoresEnum? = null,
    val products: List<ResourceProductsEditSchema>? = null,
    val processed: Boolean? = null,
    val orderNumber: Long? = null
)
