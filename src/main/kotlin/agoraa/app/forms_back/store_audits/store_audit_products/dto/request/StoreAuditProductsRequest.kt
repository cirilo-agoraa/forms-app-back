package agoraa.app.forms_back.store_audits.store_audit_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class StoreAuditProductsRequest(
    @field:NotNull(message = "Product is required")
    val product: ProductModel,

    @field:NotBlank(message = "inStore is required")
    val inStore: Boolean
)