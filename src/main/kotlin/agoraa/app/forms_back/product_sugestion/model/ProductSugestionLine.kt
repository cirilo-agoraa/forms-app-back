package agoraa.app.forms_back.product_sugestion.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "product_sugestion_line")
data class ProductSugestionLine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(name = "cost_price")
    val costPrice: Double? = null,

    @Column(name = "sale_price")
    val salePrice: Double? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sugestion_id", nullable = false)
    val productSugestion: ProductSugestionModel,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)