package agoraa.app.forms_back.supplier_registrations.supplier_registrations.dto.request

import agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.request.SupplierRegistrationWeeklyQuotationsSchema
import agoraa.app.forms_back.supplier_registrations.supplier_registration_stores.dto.request.SupplierRegistrationStoresCreateSchema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class SupplierRegistrationCreateSchema(
    // COMMON
    @field:NotNull(message = "Stores cannot be null")
    @field:Size(min = 1, message = "At least one store is required")
    val stores: List<SupplierRegistrationStoresCreateSchema>,

    @field:NotBlank(message = "Type cannot be blank")
    @field:Pattern(regexp = "REPOSICAO|COTACAO", message = "Invalid type")
    val type: String,

    @field:NotBlank(message = "CNPJ cannot be blank")
    @field:Size(min = 14, message = "CNPJ must have 14 characters")
    val cnpj: String,

    @field:NotBlank(message = "Company name cannot be blank")
    val companyName: String,

    @field:NotBlank(message = "Payment term cannot be blank")
    val paymentTerm: String,

    val sellerPhone: String? = null,
    val sellerEmail: String? = null,

    // REPOSICAO
    val factoryWebsite: String? = null,
    val exchange: Boolean? = null,
    val exchangePhysical: Boolean? = null,
    val priceTableFilePath: String? = null,
    val catalogFilePath: String? = null,
    val sample: Boolean? = null,
    val sampleDate: LocalDate? = null,
    val obs: String? = null,
    val investmentsOnStore: Boolean? = null,
    val purchaseGondola: Boolean? = null,
    val participateInInsert: Boolean? = null,
    val birthdayParty: Boolean? = null,
    val otherParticipation: Boolean? = null,
    val negotiateBonusOnFirstPurchase: Boolean? = null,

    // COTACAO
    val sellerName: String? = null,
    val supplierWebsite: String? = null,
    val minimumOrderValue: Double? = null,
    val weeklyQuotations: List<SupplierRegistrationWeeklyQuotationsSchema>? = null,
)
