package agoraa.app.forms_back.dto.supplier_registration

import agoraa.app.forms_back.shared.enums.OrderDaysEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SupplierRegistrationStoresDto(
    val id: Long,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    val deliverInStore: Boolean = false,
    val deliveryTime: Int? = null,
    val sellerName: String? = null,
    val sellerPhone: String? = null,
    val sellerEmail: String? = null,
    val orderBestDay: agoraa.app.forms_back.shared.enums.OrderDaysEnum? = null,
    val routine: String? = null,
    val motive: String? = null,
)
