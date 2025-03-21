package agoraa.app.forms_back.passive_quotations.dto.request

data class PassiveQuotationProductsCalculateRequest(
    val code: String,
    val quantity: Int?,
    val price: Double,
    val stockPlusOpenOrder: Double?,
)
