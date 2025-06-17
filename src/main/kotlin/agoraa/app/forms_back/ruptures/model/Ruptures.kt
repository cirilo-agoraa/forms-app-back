package agoraa.app.forms_back.ruptures.model

import jakarta.persistence.*
import java.time.LocalDateTime
import agoraa.app.forms_back.shared.enums.StoresEnum

@Entity
@Table(name = "rupture")
data class RupturaModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "store", nullable = false)
    val store: StoresEnum
)