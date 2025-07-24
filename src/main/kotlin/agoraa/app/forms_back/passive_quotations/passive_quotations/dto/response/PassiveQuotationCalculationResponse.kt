package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.response

import agoraa.app.forms_back.products.products.model.ProductModel

data class PassiveQuotationCalculationResponse(
    val product: ProductModel,
    val biggestSale: Int,
    val salesLastThirtyDaysSumStores: Int,
    val stockPlusOpenOrder: Double,
    val stockVix: Double,
    val stockSmj: Double,
    val stockStt: Double,
    val quantity: Double,
    val maxPurchase: Double,
    val total: Double,
    val flag1: Int,
    val flag2: Int,
    val openOrder: Boolean,
    val block: Boolean,
)
