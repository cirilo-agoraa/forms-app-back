package agoraa.app.forms_back.supplier_registrations.supplier_registrations.dto.response

import agoraa.app.forms_back.shared.enums.suppliers_registration.SuppliersRegistrationTypesEnum
import agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.response.SupplierRegistrationWeeklyQuotationsDto
import agoraa.app.forms_back.supplier_registrations.supplier_registration_stores.dto.response.SupplierRegistrationStoresDto
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
    val created: Boolean,
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
