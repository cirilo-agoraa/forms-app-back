package agoraa.app.forms_back.orders.orders.dto.request

import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class OrderRequest(
    @field:NotNull(message = "Order number cannot be null")
    val orderNumber: Long,
    @field:NotNull(message = "Supplier cannot be null")
    val supplier: String,
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,
    @field:NotNull(message = "Date created cannot be null")
    val dateCreated: LocalDateTime,
    @field:NotNull(message = "Delivery date cannot be null")
    val deliveryDate: LocalDateTime,
    @field:NotNull(message = "Received date cannot be null")
    val receivedDate: LocalDateTime,
    @field:NotNull(message = "Issued status cannot be null")
    val issued: Boolean,
    @field:NotNull(message = "Received status cannot be null")
    val received: Boolean,
    @field:NotNull(message = "Total value cannot be null")
    val totalValue: Double,
    @field:NotNull(message = "Pending value cannot be null")
    val pendingValue: Double,
    @field:NotNull(message = "Received value cannot be null")
    val receivedValue: Double
)
