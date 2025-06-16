package agoraa.app.forms_back.product_mix.dto

import java.time.LocalDateTime
import agoraa.app.forms_back.products.products.model.ProductModel

data class ProductMixRegisterRequest(
    val productId: Long,
    val foraDoMix: Boolean = false
)
data class ProductMixWithProductResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val product: ProductModel?,
    val foraDoMix: Boolean = false
)