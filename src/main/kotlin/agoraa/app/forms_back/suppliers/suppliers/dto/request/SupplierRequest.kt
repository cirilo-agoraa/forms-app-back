package agoraa.app.forms_back.suppliers.suppliers.dto.request

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.suppliers.supplier_stores.dto.request.SupplierStoresRequest
import agoraa.app.forms_back.suppliers.suppliers.enums.SupplierStatusEnum

data class SupplierRequest(
    val name: String,
    val status: SupplierStatusEnum,
    val pause: Boolean,
    val centralized: String?,
    val centralizedStore: StoresEnum?,
    val overFrequency: Boolean,
    val score: Int,
    val exchange: Boolean,
    val stores: List<SupplierStoresRequest>,
    val orderMinValue: Float?,
    val orders: Int?,
    val ordersNotDelivered: Int?,
    val ordersNotDeliveredPercentage: Float?,
    val totalValue: Float?,
    val valueReceived: Float?,
    val valueReceivedPercentage: Float?,
    val averageValueReceived: Float?,
    val minValueReceived: Float?
)