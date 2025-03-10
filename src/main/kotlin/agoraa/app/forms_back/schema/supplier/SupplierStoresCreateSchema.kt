package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

data class SupplierStoresCreateSchema(
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

    val orderDay: OrderDaysEnum? = null,
    val nextOrder: LocalDateTime? = null,
    val openOrderExpectedDelivery: LocalDateTime? = null,
)
