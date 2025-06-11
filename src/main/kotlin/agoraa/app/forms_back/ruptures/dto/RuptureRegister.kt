package agoraa.app.forms_back.ruptures.dto

import java.time.LocalDateTime
import agoraa.app.forms_back.products.products.model.ProductModel

data class RuptureRegisterRequest(
    val productId: Long
)
data class RupturaWithProductResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val product: ProductModel?
)