package agoraa.app.forms_back.schema.resource_products

data class ResourceProductsEditSchema(
    val productId: Long,
    val quantity: Int? = null,
    val qttSent: Int? = null,
    val qttReceived: Int? = null,
)
