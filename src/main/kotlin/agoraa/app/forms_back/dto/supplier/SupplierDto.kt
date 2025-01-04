package agoraa.app.forms_back.dto.supplier

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SupplierDto(
    val id: Long,
    var name: String? = null,
    var status: SupplierStatusEnum? = null,
    var orders: Int? = null,
    var ordersNotDelivered: Int?  = null,
    var ordersNotDeliveredPercentage: Float? = null,
    var totalValue: Float? = null,
    var valueReceived: Float? = null,
    var valueReceivedPercentage: Float? = null,
    var averageValueReceived: Float? = null,
    var minValueReceived: Float? = null
)
