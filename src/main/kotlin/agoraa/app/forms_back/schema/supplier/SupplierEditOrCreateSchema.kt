package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import jakarta.validation.constraints.NotBlank

data class SupplierEditOrCreateSchema(
    @field:NotBlank(message = "Name is required")
    val name: String,

    val status: SupplierStatusEnum? = null,
    val orderMinValue: Float? = null,
    val score: Int? = null,
    val exchange: Boolean? = null,
    val stores: List<SupplierStoresEditSchema>? = null,
    val orders: Int? = null,
    val ordersNotDelivered: Int? = null,
    val ordersNotDeliveredPercentage: Float? = null,
    val totalValue: Float? = null,
    val valueReceived: Float? = null,
    val valueReceivedPercentage: Float? = null,
    val averageValueReceived: Float? = null,
    val minValueReceived: Float? = null,
)