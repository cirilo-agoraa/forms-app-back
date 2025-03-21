package agoraa.app.forms_back.passive_quotations.dto.response

data class PassiveQuotationCalculationResponse(
    val name: String,
    val packageQuantity: Int,
    val biggestSale: Double,
    val stockPlusOpenOrder: Double,
    val stockVix: Double,
    val stockSmj: Double,
    val stockStt: Double,
    val finalQtt: Double,
    val maxPurchase: Double,
    val netCost: Double,
    val averageExpiration: Double,
    val total: Double,
    val flag1: Int,
    val flag2: Int,
    val openOrder: Boolean,
    val block: Boolean,
    val brand: String
)
