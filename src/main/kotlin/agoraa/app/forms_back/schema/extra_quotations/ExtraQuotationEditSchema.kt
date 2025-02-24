package agoraa.app.forms_back.schema.extra_quotations

data class ExtraQuotationEditSchema(
    val processed: Boolean? = null,
    val products: List<ExtraQuotationProductsEditSchema>? = null,
)
