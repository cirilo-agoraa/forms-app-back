package agoraa.app.forms_back.schema.extra_quotations

import agoraa.app.forms_back.model.products.ProductModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ExtraQuotationProductsCreateSchema (
    @field:NotNull(message = "Product is required")
    val product: ProductModel,

    @field:NotBlank(message = "Motive is required")
    val motive: String
)