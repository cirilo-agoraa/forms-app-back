package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.model.ProductModel

data class ExtraOrderProductEditSchema(
    val product: ProductModel? = null,
    val price: Double? = null,
    val quantity: Int? = null,
)
