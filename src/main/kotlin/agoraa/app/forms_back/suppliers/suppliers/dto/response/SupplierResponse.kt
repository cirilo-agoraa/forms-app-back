package agoraa.app.forms_back.suppliers.suppliers.dto.response

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.suppliers.supplier_stores.dto.response.SupplierStoresResponse
import agoraa.app.forms_back.suppliers.suppliers.enums.SupplierStatusEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class SupplierResponse(
    val id: Long,
    val name: String,
    val status: SupplierStatusEnum,
    val pause: Boolean,
    val centralized: String?,
    val centralizedStore: StoresEnum?,
    val overFrequency: Boolean,
    val score: Int,
    val exchange: Boolean,
    val orderMinValue: Float?,
    val orders: Int?,
    val ordersNotDelivered: Int?,
    val ordersNotDeliveredPercentage: Float?,
    val totalValue: Float?,
    val valueReceived: Float?,
    val valueReceivedPercentage: Float?,
    val averageValueReceived: Float?,
    val minValueReceived: Float?,
    var stores: List<SupplierStoresResponse>? = null
)
