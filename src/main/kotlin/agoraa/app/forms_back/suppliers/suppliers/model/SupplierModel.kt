package agoraa.app.forms_back.suppliers.suppliers.model

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.suppliers.suppliers.enums.SupplierStatusEnum
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

    @Column(nullable = false)
    val pause: Boolean,

    @Column(nullable = false)
    val centralized: String,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val centralizedStore: StoresEnum? = null,

    @Column(nullable = false)
    val overFrequency: Boolean,

    @Column(nullable = true)
    val orderMinValue: Float? = null,

    @Column(nullable = false)
    val score: Int,

    @Column(nullable = false)
    val exchange: Boolean,

    @Column(nullable = true)
    val orders: Int?,

    @Column(nullable = true)
    val ordersNotDelivered: Int?,

    @Column(nullable = true)
    val ordersNotDeliveredPercentage: Float?,

    @Column(nullable = true)
    val totalValue: Float?,

    @Column(nullable = true)
    val valueReceived: Float?,

    @Column(nullable = true)
    val valueReceivedPercentage: Float?,

    @Column(nullable = true)
    val averageValueReceived: Float?,

    @Column(nullable = true)
    val minValueReceived: Float?,
)
