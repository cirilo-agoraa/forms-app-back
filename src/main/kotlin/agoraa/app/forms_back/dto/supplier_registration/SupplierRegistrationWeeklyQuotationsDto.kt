package agoraa.app.forms_back.dto.supplier_registration

import agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum

data class SupplierRegistrationWeeklyQuotationsDto(
    val id: Long,
    val weeklyQuotation: agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum
)
