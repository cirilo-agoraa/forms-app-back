package agoraa.app.forms_back.extra_quotations.extra_quotations.dto.response

import agoraa.app.forms_back.extra_quotations.extra_quotation_products.dto.response.ExtraQuotationProductsResponse
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ExtraQuotationResponse(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    var products: List<ExtraQuotationProductsResponse>? = null
)
