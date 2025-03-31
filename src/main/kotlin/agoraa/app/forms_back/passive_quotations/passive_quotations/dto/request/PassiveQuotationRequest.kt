package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request

import agoraa.app.forms_back.enums.PaymentTermsEnum
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.enums.WppGroupsEnum
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request.PassiveQuotationProductsRequest
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel

data class PassiveQuotationRequest(
    val supplier: SupplierModel,
    val store: StoresEnum,
    val wppGroup: WppGroupsEnum,
    val paymentTerm: PaymentTermsEnum,
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
    val products: List<PassiveQuotationProductsRequest>
)
