package agoraa.app.forms_back.weekly_quotations.weekly_quotations.dto.response

import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.response.WeeklyQuotationSummariesResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class WeeklyQuotationResponse(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val sector: ProductSectorsEnum,
    var summaries: List<WeeklyQuotationSummariesResponse>? = null
)
