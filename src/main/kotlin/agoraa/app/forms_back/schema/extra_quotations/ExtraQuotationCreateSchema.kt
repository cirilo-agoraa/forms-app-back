package agoraa.app.forms_back.schema.extra_quotations

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ExtraQuotationCreateSchema(
    @field:NotNull(message = "Products are required")
    @field:Size(min = 1)
    val products: List<ExtraQuotationProductsCreateSchema>
)