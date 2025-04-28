package agoraa.app.forms_back.orders.orders.model

import agoraa.app.forms_back.shared.enums.BuyersEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class OrderModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val orderNumber: Long,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: SupplierModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val buyer: BuyersEnum,

    @Column(nullable = false)
    val dateCreated: LocalDateTime,

    @Column(nullable = false)
    val deliveryDate: LocalDateTime,

    @Column(nullable = true)
    val receivedDate: LocalDateTime? = null,

    @Column(nullable = false)
    val issued: Boolean,

    @Column(nullable = false)
    val received: Boolean,

    @Column(nullable = false)
    val totalValue: Double,

    @Column(nullable = false)
    val pendingValue: Double,

    @Column(nullable = false)
    val receivedValue: Double,

    @Column(nullable = false)
    val cancelIssued: Boolean = false,

    @Column(nullable = true)
    val cancelIssuedMotive: String? = null,
)