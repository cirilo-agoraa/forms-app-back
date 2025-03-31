package agoraa.app.forms_back.suppliers.suppliers.dto.request

import agoraa.app.forms_back.enums.supplier.SupplierStatusEnum
import agoraa.app.forms_back.suppliers.supplier_stores.dto.request.SupplierStoresRequest

data class SupplierRequest(
    val name: String,
    val status: SupplierStatusEnum,
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