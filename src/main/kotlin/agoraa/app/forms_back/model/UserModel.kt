package agoraa.app.forms_back.model

import agoraa.app.forms_back.enum.StoresEnum
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
    val password: String,

    @Column(nullable = false)
    val enabled: Boolean = true,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum
)
