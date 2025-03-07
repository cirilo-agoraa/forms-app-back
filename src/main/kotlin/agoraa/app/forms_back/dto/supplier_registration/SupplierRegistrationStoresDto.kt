package agoraa.app.forms_back.dto.supplier_registration

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
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
