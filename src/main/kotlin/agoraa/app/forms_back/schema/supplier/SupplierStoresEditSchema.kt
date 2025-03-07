package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum
import jakarta.validation.constraints.NotNull

data class SupplierStoresEditSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    val orderDay: OrderDaysEnum? = null,
    val frequency: Int? = null,
    val stock: Float? = null,
    val exchangeStock: Float? = null,
    val openOrder: Boolean? = null,
    val orderTerm: Int? = null,
    val orderMeanDeliveryTime: Float? = null,
)
