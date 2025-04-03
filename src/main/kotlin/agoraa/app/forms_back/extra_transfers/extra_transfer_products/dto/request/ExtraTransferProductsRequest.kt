package agoraa.app.forms_back.extra_transfers.extra_transfer_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ExtraTransferProductsRequest (
    @field:NotNull(message = "Product is required")
    val product: ProductModel,

    @field:NotBlank(message = "Quantity is required")
    val quantity: Int
)