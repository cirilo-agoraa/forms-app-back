package agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel

data class PassiveQuotationProductsRequest(
    val product: ProductModel,
    val price: Double,
    val quantity: Int?,
    val stockPlusOpenOrder: Double?,
)
