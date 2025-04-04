package agoraa.app.forms_back.resource_mips.resource_mip_products.model

import agoraa.app.forms_back.products.products.model.ProductModel
import agoraa.app.forms_back.resource_mips.resource_mips.model.ResourceMipModel
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
