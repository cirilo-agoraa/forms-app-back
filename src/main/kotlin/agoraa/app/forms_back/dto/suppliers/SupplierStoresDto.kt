package agoraa.app.forms_back.dto.suppliers

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum

data class SupplierStoresDto(
    val id: Long,
    val store: StoresEnum,
    val orderDay: WeekDaysEnum,
    val frequency: Int,
    val stock: Float,
    val exchangeStock: Float,
    val openOrder: Boolean,
    val orderTerm: Int,
    val orderMeanDeliveryTime: Float,
)
