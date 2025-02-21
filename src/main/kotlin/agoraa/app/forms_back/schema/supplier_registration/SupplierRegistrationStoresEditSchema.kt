package agoraa.app.forms_back.schema.supplier_registration

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum
import jakarta.validation.constraints.NotNull

data class SupplierRegistrationStoresEditSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,
    val deliverInStore: Boolean? = null,
    val deliveryTime: Int? = null,
    val sellerName: String? = null,
    val sellerPhone: String? = null,
    val orderBestDay: WeekDaysEnum? = null,
    val routine: String? = null,
    val motive: String? = null,
)