package agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request

data class PassiveQuotationProductsCalculateRequest(
    val code: String,
    val finalQtt: Int?,
    val price: Double,
    val stockPlusOpenOrder: Double?,
)
