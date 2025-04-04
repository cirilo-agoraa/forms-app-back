package agoraa.app.forms_back.resources.resource_products.model

import agoraa.app.forms_back.products.products.model.ProductModel
import agoraa.app.forms_back.resources.resources.model.ResourceModel
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    val resource: ResourceModel,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = true)
    val qttSent: Int? = null,

    @Column(nullable = true)
    val qttReceived: Int? = null,
)
