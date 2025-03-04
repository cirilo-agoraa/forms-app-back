package agoraa.app.forms_back.model.extra_transfers

import agoraa.app.forms_back.model.ProductModel
import jakarta.persistence.*

@Entity
@Table(name = "extra_transfer_products")
data class ExtraTransferProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "extra_transfer_id", nullable = false)
    val extraTransfer: ExtraTransferModel,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @Column(nullable = false)
    val quantity: Int,
)
