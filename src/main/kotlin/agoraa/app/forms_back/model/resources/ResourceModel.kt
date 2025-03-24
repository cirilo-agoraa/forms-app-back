package agoraa.app.forms_back.model.resources

import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.users.users.model.UserModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "resources")
data class ResourceModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val processed: Boolean = false,

    @Column(nullable = true)
    val orderNumber: Long? = null
)
