package agoraa.app.forms_back.orders.orders_cancel_issued_requests.model

import agoraa.app.forms_back.orders.orders.model.OrderModel
import agoraa.app.forms_back.users.users.model.UserModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders_cancel_issued_requests")
data class OrderCancelIssuedRequestModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderModel,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val motive: String,

    @Column(nullable = false)
    val processed: Boolean = false,
)
