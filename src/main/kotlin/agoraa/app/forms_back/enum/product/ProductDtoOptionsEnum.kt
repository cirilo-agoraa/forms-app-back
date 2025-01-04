package agoraa.app.forms_back.enum.product

import agoraa.app.forms_back.model.ProductModel
import kotlin.reflect.full.memberProperties

enum class ProductDtoOptionsEnum(val fields: List<String>) {
    CODE(listOf("code")),
    MINIMAL(listOf("code", "name", "store", "outOfMix", "brand")),
    ALL(ProductModel::class.memberProperties.associateBy { it.name }.keys.toList())
}