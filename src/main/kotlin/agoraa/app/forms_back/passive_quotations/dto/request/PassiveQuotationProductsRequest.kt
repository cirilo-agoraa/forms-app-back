package agoraa.app.forms_back.passive_quotations.dto.request

import agoraa.app.forms_back.model.products.ProductModel

data class PassiveQuotationProductsRequest(
    val product: ProductModel,
    val quantity: Int,
    val price: Float,
    val stockPlusOpenOrder: Float,
    val total: Float
)
