package agoraa.app.forms_back.extra_quotations.extra_quotation_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ExtraQuotationProductsRequest (
    @field:NotNull(message = "Product is required")
    val product: ProductModel,

    @field:NotBlank(message = "Motive is required")
    val motive: String
)