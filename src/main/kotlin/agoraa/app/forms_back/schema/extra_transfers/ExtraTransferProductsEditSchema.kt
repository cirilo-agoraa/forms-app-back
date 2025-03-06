package agoraa.app.forms_back.schema.extra_transfers
import agoraa.app.forms_back.model.products.ProductModel

data class ExtraTransferProductsEditSchema(
    val product: ProductModel? = null,
    val quantity: Int? = null,
)
