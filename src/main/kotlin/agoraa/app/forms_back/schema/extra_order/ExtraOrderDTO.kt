package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.enums.extra_order.OriginEnum
import agoraa.app.forms_back.enums.extra_order.PartialCompleteEnum
import java.time.LocalDate

data class ExtraOrderProductsDTO(
    val code: String,
    val price: Double,
    val quantity: Int
)

data class ExtraOrderDTO(
    val id: Long,
    val user: String,
    val supplier: String,
    val partialComplete: PartialCompleteEnum,
    val processed: Boolean = false,
    val dateSubmitted: LocalDate,
    val stores: List<String>,
    var products: List<ExtraOrderProductsDTO>? = null,
    var origin: OriginEnum? = null,
)
