package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum
import jakarta.validation.constraints.NotNull

data class SupplierStoresCreateSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    @field:NotNull(message = "Order day cannot be null")
    val orderDay: WeekDaysEnum,

    @field:NotNull(message = "Frequency cannot be null")
    val frequency: Int,

    @field:NotNull(message = "Stock cannot be null")
    val stock: Float,

    @field:NotNull(message = "Exchange Stock cannot be null")
    val exchangeStock: Float,

    @field:NotNull(message = "Open order cannot be null")
    val openOrder: Boolean,

    @field:NotNull(message = "Order term cannot be null")
    val orderTerm: Int,

    @field:NotNull(message = "Order mean delivery time cannot be null")
    val orderMeanDeliveryTime: Float,
)
