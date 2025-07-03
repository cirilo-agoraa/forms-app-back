package agoraa.app.forms_back.product_sugestion.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "product_sugestion")
data class ProductSugestionModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val description: String? = null,

    @Lob
    @Column(name = "product_image")
    val productImage: ByteArray? = null,

    @Column(nullable = false)
    val status: Int = 0,

    @Column(name = "cost_price")
    val costPrice: Double? = null,

    @Column(name = "sale_price")
    val salePrice: Double? = null,

    @Column(name = "supplier_id")
    val supplierId: Long? = null,

    @Column(name = "justification")
    val justification: String? = null,

    @Column(name = "sector")
    val sector: String? = null,

    @Column(name = "is_product_line", nullable = false)
    val isProductLine: Boolean = false, // novo campo para indicar se é uma linha

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Transient
    val lines: List<ProductSugestionLine>? = null
)