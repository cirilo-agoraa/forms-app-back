package agoraa.app.forms_back.schema.supplier

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SupplierEditMultipleSchema (
    @field:NotBlank
    val name: String,

    @field:Pattern(regexp = "ATIVO|INATIVO|COTACAO")
    val status: String? = null,

    val orders: Int?,
    val ordersNotDelivered: Int?,
    val ordersNotDeliveredPercentage: Float?,
    val totalValue: Float?,
    val valueReceived: Float?,
    val valueReceivedPercentage: Float?,
    val averageValueReceived: Float?,
    val minValueReceived: Float?
)
