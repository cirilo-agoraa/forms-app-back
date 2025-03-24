package agoraa.app.forms_back.dto.suppliers

import agoraa.app.forms_back.enums.supplier.SupplierStatusEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SupplierDto(
    val id: Long,
    val name: String,
    val status: SupplierStatusEnum,
    val orderMinValue: Float? = null,
    val score: Int,
    val exchange: Boolean,
    val orders: Int?,
    val ordersNotDelivered: Int?,
    val ordersNotDeliveredPercentage: Float?,
    val totalValue: Float?,
    val valueReceived: Float?,
    val valueReceivedPercentage: Float?,
    val averageValueReceived: Float?,
    val minValueReceived: Float?,
    var stores: List<SupplierStoresDto>? = null
)
