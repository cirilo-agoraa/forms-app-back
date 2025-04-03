package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.response

import agoraa.app.forms_back.shared.enums.PaymentTermsEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.enums.WppGroupsEnum
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.response.PassiveQuotationProductsResponse
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class PassiveQuotationResponse(
    val id: Long,
    val user: UserResponse,
    val supplier: SupplierModel,
    val createdAt: LocalDateTime,
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
    var products: List<PassiveQuotationProductsResponse>? = null
)
