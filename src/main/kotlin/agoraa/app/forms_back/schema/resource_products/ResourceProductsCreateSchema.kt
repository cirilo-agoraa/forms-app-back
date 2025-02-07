package agoraa.app.forms_back.schema.resource_products

data class ResourceProductsCreateSchema(
    val productId: Long,
    val quantity: Int,
    val qttReceived: Int? = null,
)
