package agoraa.app.forms_back.model.supplier_registrations

import agoraa.app.forms_back.shared.enums.suppliers_registration.WeeklyQuotationEnum
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
