package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum

data class SupplierSchema(
    val name: String,
    val status: SupplierStatusEnum,
    val score: Int,
    val exchange: Boolean,
    val stores: List<SupplierStoresSchema>,
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