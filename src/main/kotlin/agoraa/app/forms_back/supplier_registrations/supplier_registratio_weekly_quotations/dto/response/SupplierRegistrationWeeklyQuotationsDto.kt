package agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.response

import agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class SupplierRegistrationWeeklyQuotationsDto(
    val id: Long,
    val weeklyQuotation: WeeklyQuotationEnum
)
