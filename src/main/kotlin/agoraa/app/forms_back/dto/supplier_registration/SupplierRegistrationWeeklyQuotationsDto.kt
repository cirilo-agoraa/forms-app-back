package agoraa.app.forms_back.dto.supplier_registration

import agoraa.app.forms_back.enums.suppliers_registration.WeeklyQuotationEnum

data class SupplierRegistrationWeeklyQuotationsDto(
    val id: Long,
    val weeklyQuotation: WeeklyQuotationEnum
)
