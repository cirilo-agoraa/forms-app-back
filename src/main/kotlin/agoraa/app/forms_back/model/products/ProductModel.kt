package agoraa.app.forms_back.model.products

import agoraa.app.forms_back.enums.MipsCategoriesEnum
import agoraa.app.forms_back.enums.SectorsEnum
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.model.suppliers.SupplierModel
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

    @Column(nullable = false)
    val isResource: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: SupplierModel,

    @Column(nullable = false)
    val barcode: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum,

    @Column(nullable = false)
    val outOfMix: Boolean,

    @Column(nullable = false)
    val weight: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val sector: SectorsEnum,

    @Column(nullable = false)
    val minimumStock: Int,

    @Column(nullable = false)
    val salesLastThirtyDays: Double,

    @Column(nullable = false)
    val salesLastTwelveMonths: Double,

    @Column(nullable = false)
    val salesLastSevenDays: Double,

    @Column(nullable = false)
    val averageSalesLastThirtyDays: Double,

    @Column(nullable = false)
    val dailySales: Float,

    @Column(nullable = false)
    val lastCost: Float,

    @Column(nullable = false)
    val openOrder: Double,

    @Column(nullable = false)
    val exchangeQuantity: Float,

    @Column(nullable = true)
    val groupName: String? = null,

    @Column(nullable = true)
    val subgroup: String? = null,

    @Column(nullable = false)
    val packageQuantity: Int,

    @Column(nullable = true)
    val currentStock: Double? = null,

    @Column(nullable = true)
    val expirationDate: LocalDate? = null,

    @Column(nullable = false)
    val lossQuantity: Float,

    @Column(nullable = false)
    val averageExpiration: Double,

    @Column(nullable = false)
    val networkStock: Float,

    @Column(nullable = false)
    val transferPackage: Int,

    @Column(nullable = false)
    val promotionQuantity: Float,

    @Column(nullable = false)
    val noDeliveryQuantity: Int,

    @Column(nullable = false)
    val averageSalesLastThirtyDaysTwelveMonths: Double,

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

    @Column(nullable = false)
    val term: String,

    @Column(nullable = false)
    val currentStockPerPackage: String,

    @Column(nullable = false)
    val averageSales: Float,

    @Column(nullable = true)
    val promotionType: String? = null,

    @Column(nullable = true)
    val brand: String? = null,

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

    @Column(nullable = true)
    val category: String? = null,

    @Column(nullable = true)
    val highestSales: Double? = null,

    @Column(nullable = true)
    val totalCost: Float? = null,

    @Column(nullable = true)
    val totalSales: Float? = null,

    @Column(nullable = true, name = "cost_p")
    val costP: Float? = null,

    @Column(nullable = true, name = "sales_p")
    val salesP: Float? = null,

    @Column(nullable = false)
    val availableStock: Float,

    @Column(nullable = true)
    val stockTurnover: Float? = null,

    @Column(nullable = true)
    val netCost: Double? = null,

    @Column(nullable = true)
    val salesPrice: Float? = null,

    @Column(nullable = true)
    val salesPrice2: Float? = null,

    @Column(nullable = true)
    val promotionPrice: Float? = null,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val mipCategory: MipsCategoriesEnum? = null,
)