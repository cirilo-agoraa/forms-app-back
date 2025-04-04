package agoraa.app.forms_back.suppliers.supplier_stores.dto.response

import java.time.LocalDateTime

data class SupplierStoresResponse(
    val id: Long,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    val frequency: Int,
    val stock: Float,
    val exchangeStock: Float,
    val openOrder: Int,
    val orderTerm: Int,
    val orderMeanDeliveryTime: Float,
    val orderDay: agoraa.app.forms_back.shared.enums.OrderDaysEnum?,
    val nextOrder: LocalDateTime?,
    val openOrderExpectedDelivery: LocalDateTime?,
    val openOrderRealDelivery: LocalDateTime?,
    val sellerName: String?,
    val sellerPhone: String?,
    val sellerEmail: String?,
)
