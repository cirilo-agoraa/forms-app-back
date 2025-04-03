package agoraa.app.forms_back.schema.extra_quotations

import agoraa.app.forms_back.products.products.model.ProductModel

data class ExtraQuotationProductsEditSchema(
    val product: ProductModel? = null,
    val motive: String? = null,
)
