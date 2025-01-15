package agoraa.app.forms_back.schema.resource

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.schema.resource_products.ResourceProductsCreateSchema

data class ResourceEditSchema(
    val store: StoresEnum? = null,
    val products: List<ResourceProductsCreateSchema>? = null,
    val processed: Boolean? = null
)
