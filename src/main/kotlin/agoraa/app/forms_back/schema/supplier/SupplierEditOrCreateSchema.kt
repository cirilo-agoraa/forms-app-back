package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import jakarta.validation.constraints.NotNull

data class SupplierEditOrCreateSchema(
    @field:NotNull(message = "Name is required")
    val name: String,

    val status: SupplierStatusEnum?,
    val orderMinValue: Float?,
    val score: Int?,
    val exchange: Boolean?,
    val stores: List<SupplierStoresEditSchema>?,
    val orders: Int?,
    val ordersNotDelivered: Int?,
    val ordersNotDeliveredPercentage: Float?,
    val totalValue: Float?,
    val valueReceived: Float?,
    val valueReceivedPercentage: Float?,
    val averageValueReceived: Float?,
    val minValueReceived: Float?,
)