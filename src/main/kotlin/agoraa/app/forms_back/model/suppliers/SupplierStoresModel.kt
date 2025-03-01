package agoraa.app.forms_back.model.suppliers

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WeekDaysEnum
import jakarta.persistence.*

@Entity
@Table(name = "supplier_stores")
data class SupplierStoresModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    val supplier: SupplierModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val orderDay: WeekDaysEnum,

    @Column(nullable = false)
    val frequency: Int,

    @Column(nullable = false)
    val stock: Int,

    @Column(nullable = false)
    val openOrder: Boolean,

    @Column(nullable = false)
    val orderTerm: Int,

    @Column(nullable = false)
    val orderMeanDeliveryTime: Float,
)
