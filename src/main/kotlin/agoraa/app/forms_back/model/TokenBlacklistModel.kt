package agoraa.app.forms_back.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "token_blacklist")
data class TokenBlacklistModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val token: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @Column(nullable = false)
    val createdAt: LocalDate = LocalDate.now()
)
