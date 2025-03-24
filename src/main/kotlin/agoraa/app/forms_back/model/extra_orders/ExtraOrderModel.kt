package agoraa.app.forms_back.model.extra_orders

import agoraa.app.forms_back.enums.extra_order.OriginEnum
import agoraa.app.forms_back.enums.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.model.suppliers.SupplierModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "extra_orders")
data class ExtraOrderModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: SupplierModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val partialComplete: PartialCompleteEnum,

    @Column(nullable = false)
    val processed: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val origin: OriginEnum? = null,
)
