package agoraa.app.forms_back.store_audits.store_audit_products.dto.response

import agoraa.app.forms_back.products.products.model.ProductModel

data class StoreAuditProductsResponse(
    val id: Long,
    val product: ProductModel,
    val inStore: Boolean
)
