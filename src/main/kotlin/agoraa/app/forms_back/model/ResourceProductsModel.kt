package agoraa.app.forms_back.model

import jakarta.persistence.*

@Entity
@Table(name = "resource_products")
data class ResourceProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    val resource: ResourceModel,

    @Column(nullable = false)
    val quantity: Int
)
