package agoraa.app.forms_back.model.extra_orders

import agoraa.app.forms_back.model.products.ProductModel
import jakarta.persistence.*

@Entity
@Table(name = "extra_order_products")
data class ExtraOrderProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @ManyToOne
    @JoinColumn(name = "extra_order_id", nullable = false)
    val extraOrder: ExtraOrderModel,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val quantity: Int
)
