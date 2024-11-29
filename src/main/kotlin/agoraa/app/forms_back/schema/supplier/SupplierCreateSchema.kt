package agoraa.app.forms_back.schema.supplier

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SupplierCreateSchema(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = "ATIVO|INATIVO|COTACAO", message = "Invalid status")
    val status: String,

    val orders: Int?,
    val ordersNotDelivered: Int?,
    val ordersNotDeliveredPercentage: Float?,
    val totalValue: Float?,
    val valueReceived: Float?,
    val valueReceivedPercentage: Float?,
    val averageValueReceived: Float?,
    val minValueReceived: Float?
)
