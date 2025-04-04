package agoraa.app.forms_back.extra_quotations.extra_quotations.dto.request

import agoraa.app.forms_back.extra_quotations.extra_quotation_products.dto.request.ExtraQuotationProductsRequest
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ExtraQuotationRequest(
    @field:NotNull(message = "Products are required")
    @field:Size(min = 1)
    val products: List<ExtraQuotationProductsRequest>,
    val processed: Boolean = false,
)