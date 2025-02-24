package agoraa.app.forms_back.dto.extra_quotations

import agoraa.app.forms_back.dto.user.UserDto
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExtraQuotationDto(
    val id: Long,
    val user: UserDto,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    var products: List<ExtraQuotationProductsDto>? = null
)
