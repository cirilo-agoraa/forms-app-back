package agoraa.app.forms_back.users.users.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val enabled: Boolean = true,

    @Column(nullable = false)
    val firstAccess: Boolean = true,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: agoraa.app.forms_back.shared.enums.StoresEnum
)
