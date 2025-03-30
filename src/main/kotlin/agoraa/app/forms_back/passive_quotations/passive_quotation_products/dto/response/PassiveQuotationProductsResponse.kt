package agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.response

import agoraa.app.forms_back.model.products.ProductModel
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class PassiveQuotationProductsResponse(
    val id: Long,
    val product: ProductModel,
    val quantity: Int?,
    val stockPlusOpenOrder: Double?,
    val price: Double,
)