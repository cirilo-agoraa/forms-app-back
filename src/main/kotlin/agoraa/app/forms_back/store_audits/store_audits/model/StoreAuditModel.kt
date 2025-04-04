package agoraa.app.forms_back.store_audits.store_audits.model

import agoraa.app.forms_back.users.users.model.UserModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "store_audits")
data class StoreAuditModel(
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
