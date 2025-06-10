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

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)