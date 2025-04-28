package agoraa.app.forms_back.orders.orders.dto.response

import agoraa.app.forms_back.shared.enums.BuyersEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class OrderResponse(
    val id: Long,
    val supplier: SupplierModel,
    val orderNumber: Long,
    val buyer: BuyersEnum,
    val store: StoresEnum,
    val dateCreated: LocalDateTime,
    val deliveryDate: LocalDateTime,
    val receivedDate: LocalDateTime? = null,
    val issued: Boolean,
    val received: Boolean,
    val totalValue: Double,
    val pendingValue: Double,
    val receivedValue: Double
)
