package agoraa.app.forms_back.model

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum
import jakarta.persistence.*

@Entity
@Table(name="supplier_registration_store")
data class SupplierRegistrationStoresModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "supplier_registration_id", nullable = false)
    val supplierRegistration: SupplierRegistrationModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum,

    @Column(nullable = false)
    val deliveryTime: Int,

    @Column(nullable = true)
    val sellerName: String? = null,

    @Column(nullable = true)
    val sellerPhone: String? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val orderBestDay: WeekDaysEnum? = null,

    @Column(nullable = true)
    val routine: String? = null,

    @Column(nullable = true)
    val motive: String? = null,
)
