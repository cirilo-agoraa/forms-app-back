package agoraa.app.forms_back.extra_quotations.extra_quotation_products.dto.response

import agoraa.app.forms_back.products.products.model.ProductModel

data class ExtraQuotationProductsResponse(
    val id: Long,
    val product: ProductModel,
    val motive: String
)
