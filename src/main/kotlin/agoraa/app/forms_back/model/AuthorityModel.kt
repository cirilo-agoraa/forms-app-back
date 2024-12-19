package agoraa.app.forms_back.model

import agoraa.app.forms_back.enums.authority.AuthorityTypeEnum
import jakarta.persistence.*

@Entity
@Table(name = "authorities")
data class AuthorityModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val authority: AuthorityTypeEnum,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel
)