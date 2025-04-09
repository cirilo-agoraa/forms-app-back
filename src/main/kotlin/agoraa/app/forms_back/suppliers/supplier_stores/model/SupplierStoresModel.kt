package agoraa.app.forms_back.suppliers.supplier_stores.model

import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import jakarta.persistence.*
import java.time.LocalDateTime

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
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,

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
    val centralized: Boolean,

    @Column(nullable = false)
    val orderMeanDeliveryTime: Float,

    @Column(nullable = true)
    val sellerName: String? = null,

    @Column(nullable = true)
    val sellerPhone: String? = null,

    @Column(nullable = true)
    val sellerEmail: String? = null,

    @Column(nullable = true)
    val nextOrder: LocalDateTime? = null,

    @Column(nullable = true)
    val openOrderExpectedDelivery: LocalDateTime? = null,

    @Column(nullable = true)
    val openOrderRealDelivery: LocalDateTime? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val orderDay: agoraa.app.forms_back.shared.enums.OrderDaysEnum? = null,
)
