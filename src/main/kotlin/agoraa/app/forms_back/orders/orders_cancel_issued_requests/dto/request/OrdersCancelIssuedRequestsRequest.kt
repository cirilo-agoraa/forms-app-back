package agoraa.app.forms_back.orders.orders_cancel_issued_requests.dto.request

import agoraa.app.forms_back.orders.orders.model.OrderModel
import jakarta.validation.constraints.NotNull

data class OrdersCancelIssuedRequestsRequest(
    @field:NotNull(message = "Order cannot be null")
    val order: OrderModel,

    @field:NotNull(message = "Motive cannot be null")
    val motive: String
)
