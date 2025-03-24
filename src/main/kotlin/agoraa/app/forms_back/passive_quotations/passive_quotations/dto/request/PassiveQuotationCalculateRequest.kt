package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request

import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request.PassiveQuotationProductsCalculateRequest

data class PassiveQuotationCalculateRequest(
    val store: StoresEnum,
    val worstTerm: Int,
    val bestTerm: Int,
    val storesQuantity: Int,
    val variation: Float,
    val param1: Float,
    val param2: Float,
    val param3: Float,
    val param4: Float,
    val param5: Float,
    val param6: Float,
    val param7: Float,
    val param8: Float,
    val products: List<PassiveQuotationProductsCalculateRequest>
)
