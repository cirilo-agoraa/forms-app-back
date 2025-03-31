package agoraa.app.forms_back.suppliers.supplier_stores.dto.response

import agoraa.app.forms_back.enums.OrderDaysEnum
import agoraa.app.forms_back.enums.StoresEnum
import java.time.LocalDateTime

data class SupplierStoresResponse(
    val id: Long,
    val store: StoresEnum,
    val frequency: Int,
    val stock: Float,
    val exchangeStock: Float,
    val openOrder: Int,
    val orderTerm: Int,
    val orderMeanDeliveryTime: Float,
    val orderDay: OrderDaysEnum?,
    val nextOrder: LocalDateTime?,
    val openOrderExpectedDelivery: LocalDateTime?,
    val openOrderRealDelivery: LocalDateTime?,
    val sellerName: String?,
    val sellerPhone: String?,
    val sellerEmail: String?,
)
