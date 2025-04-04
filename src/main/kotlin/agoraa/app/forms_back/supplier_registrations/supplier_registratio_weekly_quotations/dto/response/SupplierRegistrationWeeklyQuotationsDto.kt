package agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.response

data class SupplierRegistrationWeeklyQuotationsDto(
    val id: Long,
    val weeklyQuotation: agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum
)
