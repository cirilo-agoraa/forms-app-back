package agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.response

import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.enums.WeeklyQuotationSummariesSituationEnum

data class WeeklyQuotationSummariesAnalysisResponse(
    val situation: WeeklyQuotationSummariesSituationEnum,
    val percentageAll: Double,
    val percentageSector: Double
)
