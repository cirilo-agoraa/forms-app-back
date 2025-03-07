package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import jakarta.persistence.Column
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class SupplierCreateSchema(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = "ATIVO|INATIVO|COTACAO", message = "Invalid status")
    val status: SupplierStatusEnum,

    @field:NotNull(message = "Order max value is required")
    val score: Int,

    @field:NotNull(message = "Exchange is required")
    val exchange: Boolean,

    @field:NotNull(message = "Supplier must have at least one store")
    @field:Min(value = 1, message = "Supplier must have at least one store")
    val stores: List<SupplierStoresCreateSchema>,

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
