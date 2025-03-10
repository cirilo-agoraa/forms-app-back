package agoraa.app.forms_back.dto.suppliers

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum
import java.time.LocalDate
import java.time.LocalDateTime

data class SupplierStoresDto(
    val id: Long,
    val store: StoresEnum,
    val orderDay: OrderDaysEnum? = null,
    val nextOrder: LocalDateTime? = null,
    val openOrderExpectedDelivery: LocalDateTime? = null,
    val frequency: Int,
    val stock: Float,
    val exchangeStock: Float,
    val openOrder: Int,
    val orderTerm: Int,
    val orderMeanDeliveryTime: Float,
)
