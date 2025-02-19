package agoraa.app.forms_back.model

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import jakarta.persistence.*

@Entity
@Table(name = "suppliers")
data class SupplierModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: SupplierStatusEnum,

    @Column
    val orders: Int?,

    @Column
    val ordersNotDelivered: Int?,

    @Column
    val ordersNotDeliveredPercentage: Float?,

    @Column
    val totalValue: Float?,

    @Column
    val valueReceived: Float?,

    @Column
    val valueReceivedPercentage: Float?,

    @Column
    val averageValueReceived: Float?,

    @Column
    val minValueReceived: Float?,
)
