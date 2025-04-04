package agoraa.app.forms_back.store_audits.store_audits.dto.response

import agoraa.app.forms_back.store_audits.store_audit_products.dto.response.StoreAuditProductsResponse
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class StoreAuditResponse(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    var products: List<StoreAuditProductsResponse>? = null,
)
