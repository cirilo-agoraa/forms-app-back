package agoraa.app.forms_back.model.extra_quotations

import agoraa.app.forms_back.model.products.ProductModel
import jakarta.persistence.*

@Entity
@Table(name = "extra_quotation_products")
data class ExtraQuotationProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "extra_quotation_id", nullable = false)
    val extraQuotation: ExtraQuotationModel,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @Column(nullable = false)
    val motive: String,
)
