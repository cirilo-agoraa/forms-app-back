package agoraa.app.forms_back.orders.orders.dto.request

data class OrderPatchRequest(
    val cancelIssued: Boolean? = null,
    val cancelIssuedMotive: String? = null,
)
