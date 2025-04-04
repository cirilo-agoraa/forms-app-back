package agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class SupplierRegistrationWeeklyQuotationsSchema(
    @field:NotNull(message = "Weekly quotation cannot be null")
    @field:Pattern(regexp = "MERCERIA|LIMPEZA|HIGIENE", message = "Invalid weekly quotation")
    val weeklyQuotation: agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum,
)