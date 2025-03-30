package agoraa.app.forms_back.extra_transfers.extra_transfer_products.dto.response

import agoraa.app.forms_back.model.products.ProductModel

data class ExtraTransferProductsResponse(
    val id: Long,
    val product: ProductModel,
    val quantity: Int
)
