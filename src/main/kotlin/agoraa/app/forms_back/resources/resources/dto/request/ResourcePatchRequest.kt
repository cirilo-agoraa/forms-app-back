package agoraa.app.forms_back.resources.resources.dto.request

import agoraa.app.forms_back.resources.resource_products.dto.request.ResourceProductsPatchRequest

data class ResourcePatchRequest(
    val orderNumber: Long? = null,
    val processed: Boolean? = null,
    val products: List<ResourceProductsPatchRequest>? = null
)
