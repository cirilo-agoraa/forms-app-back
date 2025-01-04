package agoraa.app.forms_back.model

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "extra_order_products")
data class ExtraOrderProductModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @ManyToOne
    @JoinColumn(name = "extra_order_id", nullable = false)
    @JsonBackReference
    val extraOrder: ExtraOrderModel,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = false)
    var quantity: Int
)
