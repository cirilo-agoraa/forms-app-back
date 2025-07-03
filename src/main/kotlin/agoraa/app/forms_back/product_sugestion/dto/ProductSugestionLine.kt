package agoraa.app.forms_back.product_sugestion.dto
import java.time.LocalDateTime

data class ProductSugestionLineRequest(
    val name: String,
    val costPrice: Double? = null,
    val salePrice: Double? = null,
    val productSugestionId: Long,
)

data class ProductSugestionLineResponse(
    val id: Long,
    val name: String,
    val costPrice: Double?,
    val salePrice: Double?,
    val createdAt: LocalDateTime
)