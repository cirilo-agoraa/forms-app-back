package agoraa.app.forms_back.schema.supplier_registration_stores

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum

data class SupplierRegistrationStoresCreateSchema(
    val store: StoresEnum,
    val deliveryTime: Int? = null,
    val sellerName: String? = null,
    val sellerPhone: String? = null,
    val orderBestDay: WeekDaysEnum? = null,
    val routine: String? = null,
    val motive: String? = null,
)
