package agoraa.app.forms_back.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "products")
data class ProductModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val code: String,

    @Column(nullable = false)
    val name: String,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: SupplierModel,

    @Column(nullable = false)
    val barcode: String,

    @Column(nullable = false)
    val store: String,

    @Column(nullable = false)
    val outOfMix: Boolean,

    @Column(nullable = false)
    val weight: String,

    @Column(nullable = false)
    val sector: String,

    @Column(nullable = true)
    val groupName: String? = null,

    @Column(nullable = true)
    val subgroup: String? = null,

    @Column(nullable = true)
    val packageQuantity: Int? = null,

    @Column(nullable = false)
    val minimumStock: Int,

    @Column(nullable = false)
    val salesLast30Days: Float,

    @Column(nullable = false)
    val salesLast12Months: Float,

    @Column(nullable = false)
    val salesLast7Days: Float,

    @Column(nullable = false)
    val dailySales: Float,

    @Column(nullable = false)
    val lastCost: Float,

    @Column(nullable = false)
    val averageSalesLast30Days: Float,

    @Column(nullable = true)
    val currentStock: Float? = null,

    @Column(nullable = false)
    val openOrder: Float,

    @Column(nullable = true)
    val expirationDate: LocalDate? = null,

    @Column(nullable = false)
    val lossQuantity: Float,

    @Column(nullable = true)
    val promotionType: String? = null,

    @Column(nullable = true)
    val brand: String? = null,

    @Column(nullable = false)
    val exchangeQuantity: Float,

    @Column(nullable = true)
    val flag1: String? = null,

    @Column(nullable = true)
    val flag2: String? = null,

    @Column(nullable = true)
    val flag3: String? = null,

    @Column(nullable = true)
    val flag4: String? = null,

    @Column(nullable = true)
    val flag5: String? = null,

    @Column(nullable = false)
    val averageExpiration: Float,

    @Column(nullable = false)
    val networkStock: Float,

    @Column(nullable = false)
    val transferPackage: Int,

    @Column(nullable = false)
    val promotionQuantity: Float,

    @Column(nullable = true)
    val category: String? = null,

    @Column(nullable = false)
    val noDeliveryQuantity: Int,

    @Column(nullable = false)
    val averageSales30d12m: Float,

    @Column(nullable = true)
    val highestSales: Float? = null,

    @Column(nullable = false)
    val dailySalesAmount: Float,

    @Column(nullable = false)
    val daysToExpire: Float,

    @Column(nullable = false)
    val salesProjection: Float,

    @Column(nullable = false)
    val inProjection: Int,

    @Column(nullable = false)
    val excessStock: Float,

    @Column(nullable = true)
    val totalCost: Float? = null,

    @Column(nullable = true)
    val totalSales: Float? = null,

    @Column(nullable = false)
    val term: String,

    @Column(nullable = false)
    val currentStockPerPackage: String,

    @Column(nullable = false)
    val averageSales: Float,

    @Column(nullable = true, name = "cost_p")
    val costP: Float? = null,

    @Column(nullable = true, name = "sales_p")
    val salesP: Float? = null,

    @Column(nullable = false)
    val availableStock: Float,

    @Column(nullable = true)
    val stockTurnover: Float? = null,

    @Column(nullable = true)
    val netCost: Float? = null,

    @Column(nullable = true)
    val salesPrice: Float? = null,

    @Column(nullable = true)
    val salesPrice2: Float? = null,

    @Column(nullable = true)
    val promotionPrice: Float? = null
)