package agoraa.app.forms_back.passive_quotations.passive_quotation_products.model

import agoraa.app.forms_back.products.products.model.ProductModel
import agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel
import jakarta.persistence.*

@Entity
@Table(name = "passive_quotation_products")
data class PassiveQuotationProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "passive_quotation_id", nullable = false)
    val passiveQuotation: PassiveQuotationModel,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = true)
    val quantity: Int? = null,

    @Column(nullable = true)
    val stockPlusOpenOrder: Double? = null,
)