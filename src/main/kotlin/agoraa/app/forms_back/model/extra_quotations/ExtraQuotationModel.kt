package agoraa.app.forms_back.model.extra_quotations

import agoraa.app.forms_back.model.UserModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "extra_quotations")
data class ExtraQuotationModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val processed: Boolean = false,
)