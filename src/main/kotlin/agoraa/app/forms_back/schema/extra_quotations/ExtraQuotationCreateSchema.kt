package agoraa.app.forms_back.schema.extra_quotations

import jakarta.validation.constraints.Size
import jakarta.validation.constraints.NotNull

data class ExtraQuotationCreateSchema(
    @field:NotNull(message = "Products are required")
    @field:Size(min = 1)
    val products: List<ExtraQuotationProductsCreateSchema>
)