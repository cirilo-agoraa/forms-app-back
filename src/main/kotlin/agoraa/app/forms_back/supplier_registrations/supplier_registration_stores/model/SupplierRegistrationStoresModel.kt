package agoraa.app.forms_back.supplier_registrations.supplier_registration_stores.model

import agoraa.app.forms_back.supplier_registrations.supplier_registrations.model.SupplierRegistrationModel
import jakarta.persistence.*

@Entity
@Table(name = "supplier_registration_stores")
data class SupplierRegistrationStoresModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_registration_id", nullable = false)
    val supplierRegistration: SupplierRegistrationModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,

    @Column(nullable = false)
    val deliverInStore: Boolean = false,

    @Column(nullable = true)
    val deliveryTime: Int? = null,

    @Column(nullable = true)
    val sellerName: String? = null,

    @Column(nullable = true)
    val sellerPhone: String? = null,

    @Column(nullable = true)
    val sellerEmail: String? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val orderBestDay: agoraa.app.forms_back.shared.enums.OrderDaysEnum? = null,

    @Column(nullable = true)
    val routine: String? = null,

    @Column(nullable = true)
    val motive: String? = null,
)
