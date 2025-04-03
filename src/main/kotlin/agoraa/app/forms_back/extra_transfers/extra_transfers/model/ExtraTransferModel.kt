package agoraa.app.forms_back.extra_transfers.extra_transfers.model

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.users.users.model.UserModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "extra_transfers")
data class ExtraTransferModel (
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

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val originStore: agoraa.app.forms_back.shared.enums.StoresEnum,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val destinyStore: agoraa.app.forms_back.shared.enums.StoresEnum,
)