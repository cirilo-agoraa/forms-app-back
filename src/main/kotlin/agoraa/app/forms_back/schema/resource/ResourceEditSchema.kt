package agoraa.app.forms_back.schema.resource

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.schema.resource_products.ResourceProductsEditSchema

data class ResourceEditSchema(
    val store: StoresEnum? = null,
    val products: List<ResourceProductsEditSchema>? = null,
    val processed: Boolean? = null,
    val orderNumber: Long? = null
)
