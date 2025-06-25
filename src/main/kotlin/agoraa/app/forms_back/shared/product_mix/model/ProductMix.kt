package agoraa.app.forms_back.product_mix.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "product_mix")
data class ProductMixModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "fora_do_mix", nullable = false)
    val foraDoMix: Boolean = false,
    
    @Column(name = "store")
    val store: String? = "AMBAS",

    @Column(name = "motive")
    val motive: String? = ""
)