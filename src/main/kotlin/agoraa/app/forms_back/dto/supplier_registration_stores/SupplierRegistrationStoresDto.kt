package agoraa.app.forms_back.dto.supplier_registration_stores

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum

data class SupplierRegistrationStoresDto (
    val id: Long,
    val store: StoresEnum,
    val deliveryTime: Int,
    val sellerName: String?,
    val sellerPhone: String?,
    val orderBestDay: WeekDaysEnum?,
    val routine: String?,
    val motive: String?,
)
