package agoraa.app.forms_back.enum.supplier

import agoraa.app.forms_back.model.SupplierModel
import kotlin.reflect.full.memberProperties

enum class SupplierDtoOptionsEnum(val fields: List<String>) {
    NAME(listOf("name")),
    MINIMAL(listOf("name", "status")),
    ALL(agoraa.app.forms_back.model.SupplierModel::class.memberProperties.associateBy { it.name }.keys.toList())
}