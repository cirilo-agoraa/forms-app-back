package agoraa.app.forms_back.product_sugestion.dto

data class ProductSugestionRequest(
    val name: String,
    val description: String? = null,
    val status: Int = 0,
    val productImage: String? = null,         // base64 string opcional
    val costPrice: Double? = null,
    val salePrice: Double? = null,
    val supplierId: Long? = null,
    val justification: String? = null,
    val sector: String? = null,
)