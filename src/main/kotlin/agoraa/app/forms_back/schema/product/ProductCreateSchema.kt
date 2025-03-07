package agoraa.app.forms_back.schema.product

import agoraa.app.forms_back.enum.MipsCategoriesEnum
import agoraa.app.forms_back.enum.SectorsEnum
import agoraa.app.forms_back.enum.StoresEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ProductCreateSchema(
    @field:NotNull(message = "Product code is required")
    val code: String,

    @field:NotBlank(message = "Product name is required")
    val name: String,

    @field:NotBlank(message = "Supplier name is required")
    val supplier: String,

    @field:NotNull(message = "Product barcode is required")
    val barcode: String,

    @field:NotBlank(message = "Store name is required")
    val store: StoresEnum,

    @field:NotNull(message = "Out of mix is required")
    val outOfMix: Boolean,

    @field:NotBlank(message = "Product weight is required")
    val weight: String,

    @field:NotBlank(message = "Product sector is required")
    val sector: SectorsEnum,

    val groupName: String? = null,

    val subgroup: String? = null,

    val packageQuantity: Int? = null,

    @field:NotNull(message = "Minimum stock is required")
    val minimumStock: Int,

    @field:NotNull(message = "Sales last 30 days is required")
    val salesLast30Days: Float,

    @field:NotNull(message = "Sales last 12 months is required")
    val salesLast12Months: Float,

    @field:NotNull(message = "Sales last 7 days is required")
    val salesLast7Days: Float,

    @field:NotNull(message = "Daily sales is required")
    val dailySales: Float,

    @field:NotNull(message = "Last cost is required")
    val lastCost: Float,

    @field:NotNull(message = "Average sales last 30 days is required")
    val averageSalesLast30Days: Float,

    val currentStock: Float? = null,

    @field:NotNull(message = "Open order is required")
    val openOrder: Float,

    val expirationDate: LocalDate? = null,

    @field:NotNull(message = "Loss quantity is required")
    val lossQuantity: Float,

    val promotionType: String? = null,

    val brand: String? = null,

    @field:NotNull(message = "Exchange quantity is required")
    val exchangeQuantity: Float,

    val flag1: String? = null,

    val flag2: String? = null,

    val flag3: String? = null,

    val flag4: String? = null,

    val flag5: String? = null,

    @field:NotNull(message = "Average expiration is required")
    val averageExpiration: Float,

    @field:NotNull(message = "Network stock is required")
    val networkStock: Float,

    @field:NotNull(message = "Transfer package is required")
    val transferPackage: Int,

    @field:NotNull(message = "Promotion quantity is required")
    val promotionQuantity: Float,

    val category: String? = null,

    @field:NotNull(message = "No delivery quantity is required")
    val noDeliveryQuantity: Int,

    @field:NotNull(message = "Average sales 30 days 12 months is required")
    val averageSales30d12m: Float,

    val highestSales: Float? = null,

    @field:NotNull(message = "Daily sales amount is required")
    val dailySalesAmount: Float,

    @field:NotNull(message = "Days to expire is required")
    val daysToExpire: Float,

    @field:NotNull(message = "Sales projection is required")
    val salesProjection: Float,

    @field:NotNull(message = "In projection is required")
    val inProjection: Int,

    @field:NotNull(message = "Excess stock is required")
    val excessStock: Float,

    val totalCost: Float? = null,

    val totalSales: Float? = null,

    @field:NotBlank(message = "Term is required")
    val term: String,

    @field:NotBlank(message = "Current stock per package is required")
    val currentStockPerPackage: String,

    @field:NotNull(message = "Average sales is required")
    val averageSales: Float,

    val costP: Float? = null,

    val salesP: Float? = null,

    @field:NotNull(message = "Available stock is required")
    val availableStock: Float,

    val stockTurnover: Float? = null,

    val netCost: Float? = null,

    val salesPrice: Float? = null,

    val salesPrice2: Float? = null,

    val promotionPrice: Float? = null,

    val mipCategory: MipsCategoriesEnum? = null

)