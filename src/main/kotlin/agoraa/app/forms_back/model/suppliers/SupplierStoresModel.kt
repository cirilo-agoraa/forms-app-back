package agoraa.app.forms_back.model.suppliers

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum
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
    val frequency: Int,

    @Column(nullable = false)
    val stock: Float,

    @Column(nullable = false)
    val exchangeStock: Float,

    @Column(nullable = false)
    val openOrder: Int,

    @Column(nullable = false)
    val orderTerm: Int,

    @Column(nullable = false)
    val orderMeanDeliveryTime: Float,

    @Column
    @Enumerated(EnumType.STRING)
    val orderDay: OrderDaysEnum? = null,
)
