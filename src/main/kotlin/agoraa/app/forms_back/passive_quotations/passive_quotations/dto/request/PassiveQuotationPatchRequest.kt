package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request

data class PassiveQuotationPatchRequest(
    val status: Int,
    val createOrder: Boolean
)
