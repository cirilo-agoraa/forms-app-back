package agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request

import agoraa.app.forms_back.model.products.ProductModel

data class PassiveQuotationProductsRequest(
    val product: ProductModel,
    val price: Double,
    val quantity: Int?,
    val stockPlusOpenOrder: Double?,
)
