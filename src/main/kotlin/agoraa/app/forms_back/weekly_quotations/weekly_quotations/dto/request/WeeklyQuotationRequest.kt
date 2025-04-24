package agoraa.app.forms_back.weekly_quotations.weekly_quotations.dto.request

import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.request.WeeklyQuotationSummariesRequest
import java.time.LocalDateTime

data class WeeklyQuotationRequest(
    val sector: ProductSectorsEnum,
    val quotationDate: LocalDateTime,
    val summaries: List<WeeklyQuotationSummariesRequest>
)
