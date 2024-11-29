package agoraa.app.forms_back.model

import jakarta.persistence.*

@Entity
@Table(name = "extra_order_products")
data class ExtraOrderProductModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,

    @Column(nullable = false)
    val code: String,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val quantity: Int,

    @ManyToOne
    @JoinColumn(name = "extra_order_id")
    val extraOrder: ExtraOrderModel
)
