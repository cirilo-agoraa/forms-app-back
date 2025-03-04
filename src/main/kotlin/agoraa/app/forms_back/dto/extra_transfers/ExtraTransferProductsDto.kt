package agoraa.app.forms_back.dto.extra_transfers

import agoraa.app.forms_back.model.ProductModel

data class ExtraTransferProductsDto(
    val id: Long,
    val product: ProductModel,
    val quantity: Int
)
