package agoraa.app.forms_back.schema.supplier_registration

import agoraa.app.forms_back.enum.suppliers_registration.SuppliersRegistrationTypesEnum
import agoraa.app.forms_back.enum.suppliers_registration.WeeklyQuotationEnum
import agoraa.app.forms_back.schema.supplier_registration_stores.SupplierRegistrationStoresCreateSchema
import java.time.LocalDate

data class SupplierRegistrationEditSchema(
    val stores: List<SupplierRegistrationStoresCreateSchema>? = null,
    val type: SuppliersRegistrationTypesEnum? = null,
    val cnpj: String? = null,
    val companyName: String? = null,
    val paymentTerm: String? = null,
    val sellerName: String? = null,
    val sellerPhone: String? = null,
    val sellerEmail: String? = null,
    val address: String? = null,
    val factoryWebsite: String? = null,
    val exchange: Boolean? = null,
    val exchangePhysical: Boolean? = null,
    val priceTableFilePath: String? = null,
    val catalogFilePath: String? = null,
    val sampleDate: LocalDate? = null,
    val obs: String? = null,
    val supplierWebsite: String? = null,
    val minimumOrderValue: Double? = null,
    val weeklyQuotation: WeeklyQuotationEnum? = null,
    val investmentsOnStore: Boolean? = null,
    val purchaseGondola: Boolean? = null,
    val participateInInsert: Boolean? = null,
    val birthdayParty: Boolean? = null,
    val otherParticipation: Boolean? = null,
    val negotiateBonusOnFirstPurchase: Boolean? = null
)
