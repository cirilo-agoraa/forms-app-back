package agoraa.app.forms_back.dto.supplier_registration

import agoraa.app.forms_back.enums.suppliers_registration.SuppliersRegistrationTypesEnum
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class SupplierRegistrationDto(
    val id: Long,
    val user: UserResponse,
    val accepted: Boolean,
    val createdAt: LocalDateTime,
    val type: SuppliersRegistrationTypesEnum,
    val companyName: String,
    var stores: List<SupplierRegistrationStoresDto>? = null,
    var weeklyQuotations: List<SupplierRegistrationWeeklyQuotationsDto>? = null,
    var cnpj: String? = null,
    var paymentTerm: String? = null,
    var sellerPhone: String? = null,
    var sellerEmail: String? = null,
    var sellerName: String? = null,
    var factoryWebsite: String? = null,
    var exchange: Boolean? = null,
    var exchangePhysical: Boolean? = null,
    var priceTableFilePath: String? = null,
    var catalogFilePath: String? = null,
    var sample: Boolean? = null,
    var sampleDate: LocalDate? = null,
    var sampleArrivedInVix: Boolean? = null,
    var obs: String? = null,
    var supplierWebsite: String? = null,
    var minimumOrderValue: Double? = null,
    var investmentsOnStore: Boolean? = null,
    var purchaseGondola: Boolean? = null,
    var participateInInsert: Boolean? = null,
    var birthdayParty: Boolean? = null,
    var otherParticipation: Boolean? = null,
    var negotiateBonusOnFirstPurchase: Boolean? = null
)
