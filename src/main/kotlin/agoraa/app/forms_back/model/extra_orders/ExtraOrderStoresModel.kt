package agoraa.app.forms_back.model.extra_orders

import agoraa.app.forms_back.enums.StoresEnum
import jakarta.persistence.*

@Entity
@Table(name = "extra_order_stores")
data class ExtraOrderStoresModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extra_order_id", nullable = false)
    val extraOrder: ExtraOrderModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum
)