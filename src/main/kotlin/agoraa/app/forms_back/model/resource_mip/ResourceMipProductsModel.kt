package agoraa.app.forms_back.model.resource_mip

import agoraa.app.forms_back.model.products.ProductModel
import jakarta.persistence.*

@Entity
@Table(name = "resource_mip_products")
data class ResourceMipProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "resource_mip_id", nullable = false)
    val resourceMip: ResourceMipModel,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @Column(nullable = false)
    val quantity: Int
)
