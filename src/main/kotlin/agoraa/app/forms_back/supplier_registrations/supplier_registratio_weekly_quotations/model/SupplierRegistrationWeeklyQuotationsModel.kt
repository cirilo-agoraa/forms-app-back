package agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.model

import agoraa.app.forms_back.supplier_registrations.supplier_registrations.model.SupplierRegistrationModel
import jakarta.persistence.*

@Entity
@Table(name = "supplier_registration_weekly_quotations")
data class SupplierRegistrationWeeklyQuotationsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val weeklyQuotation: agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_registration_id", nullable = false)
    val supplierRegistration: SupplierRegistrationModel,
)
