package agoraa.app.forms_back.weekly_quotations.weekly_quotations.model

import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.users.users.model.UserModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "weekly_quotations")
data class WeeklyQuotationModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable=false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    val sector: ProductSectorsEnum
)

