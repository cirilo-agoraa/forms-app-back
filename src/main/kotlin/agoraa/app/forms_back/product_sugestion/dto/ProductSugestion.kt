package agoraa.app.forms_back.product_sugestion.dto

data class ProductSugestionRequest(
    val name: String,
    val description: String? = null,
    val status: Int = 0

)