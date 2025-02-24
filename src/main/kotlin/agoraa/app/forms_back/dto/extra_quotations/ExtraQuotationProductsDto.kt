package agoraa.app.forms_back.dto.extra_quotations

import agoraa.app.forms_back.model.ProductModel

data class ExtraQuotationProductsDto(
    val id: Long,
    val product: ProductModel,
    val motive: String
)
