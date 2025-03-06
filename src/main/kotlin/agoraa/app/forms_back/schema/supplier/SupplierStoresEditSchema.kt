package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum
import jakarta.validation.constraints.NotNull

data class SupplierStoresEditSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    val orderDay: WeekDaysEnum? = null,
    val frequency: Int? = null,
    val stock: Float? = null,
    val exchangeStock: Float? = null,
    val openOrder: Boolean? = null,
    val orderTerm: Int? = null,
    val orderMeanDeliveryTime: Float? = null,
)
