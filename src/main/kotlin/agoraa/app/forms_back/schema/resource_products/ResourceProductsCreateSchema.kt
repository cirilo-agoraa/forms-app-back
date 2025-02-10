package agoraa.app.forms_back.schema.resource_products

data class ResourceProductsCreateSchema(
    val productId: Long,
    val quantity: Int,
    val qttSent: Int? = null,
    val qttReceived: Int? = null,
)
