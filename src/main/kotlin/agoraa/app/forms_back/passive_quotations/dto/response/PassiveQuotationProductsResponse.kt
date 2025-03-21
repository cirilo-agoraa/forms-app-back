package agoraa.app.forms_back.passive_quotations.dto.response

import agoraa.app.forms_back.model.products.ProductModel

data class PassiveQuotationProductsResponse(
    val id: Long,
    val product: ProductModel,
    val quantity: Int,
    val price: Float,
    val stockPlusOpenOrder: Float,
    val total: Float
)