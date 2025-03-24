package agoraa.app.forms_back.users.user_roles.model

import agoraa.app.forms_back.enums.RolesEnum
import agoraa.app.forms_back.users.users.model.UserModel
import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "authorities")
data class AuthorityModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val authority: RolesEnum,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    val user: UserModel
)