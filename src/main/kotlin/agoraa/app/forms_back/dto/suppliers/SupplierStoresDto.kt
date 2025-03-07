package agoraa.app.forms_back.dto.suppliers

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum

data class SupplierStoresDto(
    val id: Long,
    val store: StoresEnum,
    val orderDay: OrderDaysEnum? = null,
    val frequency: Int,
    val stock: Float,
    val exchangeStock: Float,
    val openOrder: Boolean,
    val orderTerm: Int,
    val orderMeanDeliveryTime: Float,
)
