package agoraa.app.forms_back.dto.extra_quotations

import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ExtraQuotationDto(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    var products: List<ExtraQuotationProductsDto>? = null
)
