package agoraa.app.forms_back.store_audits.store_audits.dto.request

import agoraa.app.forms_back.store_audits.store_audit_products.dto.request.StoreAuditProductsRequest
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class StoreAuditRequest(
    @field:NotNull(message = "Products are required")
    @field:Size(min = 1)
    val products: List<StoreAuditProductsRequest>,

    val processed: Boolean = false
)
