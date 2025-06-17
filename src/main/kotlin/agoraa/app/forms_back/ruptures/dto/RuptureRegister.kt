package agoraa.app.forms_back.ruptures.dto

import java.time.LocalDateTime
import agoraa.app.forms_back.products.products.model.ProductModel
import agoraa.app.forms_back.shared.enums.StoresEnum

data class RuptureRegisterRequest(
    val productId: Long,
    val store: String
)
data class RupturaWithProductResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val product: ProductModel?,
    val store: StoresEnum?

)