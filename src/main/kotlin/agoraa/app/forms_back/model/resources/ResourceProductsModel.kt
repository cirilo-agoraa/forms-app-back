package agoraa.app.forms_back.model.resources

import agoraa.app.forms_back.model.ProductModel
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "resource_products")
data class ResourceProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val product: ProductModel,

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    val resource: ResourceModel,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = true)
    val qttSent: Int? = null,

    @Column(nullable = true)
    val qttReceived: Int? = null,
)
