package agoraa.app.forms_back.schema.extra_transfers

import agoraa.app.forms_back.model.ProductModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ExtraTransferProductsCreateSchema (
    @field:NotNull(message = "Product is required")
    val product: ProductModel,

    @field:NotBlank(message = "Quantity is required")
    val quantity: Int
)