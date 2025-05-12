package agoraa.app.forms_back.supplier_registrations.supplier_registration_stores.dto.response

import agoraa.app.forms_back.shared.enums.OrderDaysEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class SupplierRegistrationStoresDto(
    val id: Long,
    val store: StoresEnum,
    val deliverInStore: Boolean = false,
    val deliveryTime: Int? = null,
    val sellerName: String? = null,
    val sellerPhone: String? = null,
    val sellerEmail: String? = null,
    val orderBestDay: OrderDaysEnum? = null,
    val routine: String? = null,
    val motive: String? = null,
)
