package agoraa.app.forms_back.model

import agoraa.app.forms_back.enums.StoresEnum
import jakarta.persistence.*

@Entity
@Table(name = "extra_order_store")
class ExtraOrderStoreModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "extra_order_id", nullable = false)
    val extraOrder: ExtraOrderModel,

    @Column(nullable = false)
    val store: StoresEnum
)