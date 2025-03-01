package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum

data class SupplierEditSchema(
    val name: String? = null,
    val orderMinValue: Float? = null,
    val score: Int? = null,
    val exchange: Boolean? = null,
    val stores: List<SupplierStoresEditSchema>? = null,
    val status: SupplierStatusEnum? = null,
    val orders: Int? = null,
    val ordersNotDelivered: Int? = null,
    val ordersNotDeliveredPercentage: Float? = null,
    val totalValue: Float? = null,
    val valueReceived: Float? = null,
    val valueReceivedPercentage: Float? = null,
    val averageValueReceived: Float? = null,
    val minValueReceived: Float? = null,
)