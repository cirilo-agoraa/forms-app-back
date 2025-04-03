package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request.PassiveQuotationProductsCalculateRequest

data class PassiveQuotationCalculateRequest(
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    val worstTerm: Int,
    val bestTerm: Int,
    val storesQuantity: Int,
    val variation: Double,
    val param1: Double,
    val param2: Double,
    val param3: Double,
    val param4: Double,
    val param5: Double,
    val param6: Double,
    val param7: Double,
    val param8: Double,
    val products: List<PassiveQuotationProductsCalculateRequest>
)
