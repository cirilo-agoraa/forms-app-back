package agoraa.app.forms_back.model.supplier_registrations

import agoraa.app.forms_back.enums.suppliers_registration.SuppliersRegistrationTypesEnum
import agoraa.app.forms_back.users.users.model.UserModel
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "supplier_registrations")
data class SupplierRegistrationModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val accepted: Boolean = false,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: SuppliersRegistrationTypesEnum,

    @Column(nullable = false)
    val companyName: String,

    @Column(nullable = false)
    val cnpj: String,

    @Column(nullable = false)
    val paymentTerm: String,

    @Column(nullable = true)
    val sellerEmail: String? = null,

    @Column(nullable = true)
    val sellerPhone: String? = null,

    @Column(nullable = true)
    val sellerName: String? = null,

    @Column(nullable = true)
    val factoryWebsite: String? = null,

    @Column(nullable = true)
    val exchange: Boolean? = null,

    @Column(nullable = true)
    val exchangePhysical: Boolean? = null,

    @Column(nullable = true)
    val priceTableFilePath: String? = null,

    @Column(nullable = true)
    val catalogFilePath: String? = null,

    @Column(nullable = true)
    val sample: Boolean? = null,

    @Column(nullable = true)
    val sampleDate: LocalDate? = null,

    @Column(nullable = true)
    val sampleArrivedInVix: Boolean? = null,

    @Column(nullable = true)
    val obs: String? = null,

    @Column(nullable = true)
    val supplierWebsite: String? = null,

    @Column(nullable = true)
    val minimumOrderValue: Double? = null,

    @Column(nullable = true)
    val investmentsOnStore: Boolean? = null,

    @Column(nullable = true)
    val purchaseGondola: Boolean? = null,

    @Column(nullable = true)
    val participateInInsert: Boolean? = null,

    @Column(nullable = true)
    val birthdayParty: Boolean? = null,

    @Column(nullable = true)
    val otherParticipation: Boolean? = null,

    @Column(nullable = true)
    val negotiateBonusOnFirstPurchase: Boolean? = null
)
