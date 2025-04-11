package agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.request

import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.enums.WeeklyQuotationSummariesSituationEnum

data class WeeklyQuotationSummariesRequest(
    val situation: WeeklyQuotationSummariesSituationEnum,
    val quantity: Int,
    val percentage: Double? = null
)
