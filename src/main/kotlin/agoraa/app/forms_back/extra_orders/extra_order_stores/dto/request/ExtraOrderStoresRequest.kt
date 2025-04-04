package agoraa.app.forms_back.extra_orders.extra_order_stores.dto.request

import jakarta.validation.constraints.NotNull

data class ExtraOrderStoresRequest(
    @field:NotNull(message = "Stores cannot be null")
    val store: agoraa.app.forms_back.shared.enums.StoresEnum
)
