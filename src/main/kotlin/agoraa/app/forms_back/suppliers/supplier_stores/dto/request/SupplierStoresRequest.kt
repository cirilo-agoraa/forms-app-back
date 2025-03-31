package agoraa.app.forms_back.suppliers.supplier_stores.dto.request

import agoraa.app.forms_back.enums.OrderDaysEnum
import agoraa.app.forms_back.enums.StoresEnum
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class SupplierStoresRequest(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    @field:NotNull(message = "Frequency cannot be null")
    val frequency: Int,

    @field:NotNull(message = "Stock cannot be null")
    val stock: Float,

    @field:NotNull(message = "Exchange Stock cannot be null")
    val exchangeStock: Float,

    @field:NotNull(message = "Open order cannot be null")
    val openOrder: Int,

    @field:NotNull(message = "Order term cannot be null")
    val orderTerm: Int,

    @field:NotNull(message = "Order mean delivery time cannot be null")
    val orderMeanDeliveryTime: Float,

    val orderDay: OrderDaysEnum?,
    val nextOrder: LocalDateTime?,
    val openOrderExpectedDelivery: LocalDateTime?,
    val openOrderRealDelivery: LocalDateTime?,
    val sellerName: String?,
    val sellerPhone: String?,
    val sellerEmail: String?,
)
