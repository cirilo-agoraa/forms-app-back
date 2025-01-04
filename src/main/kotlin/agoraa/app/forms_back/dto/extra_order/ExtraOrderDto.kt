package agoraa.app.forms_back.dto.extra_order

import agoraa.app.forms_back.enum.extra_order.OriginEnum
import agoraa.app.forms_back.enum.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.model.ExtraOrderProductModel

data class ExtraOrderDto(
    val id: Long,
    val supplier: String,
    val user: String,
    val processed: Boolean,
    val dateSubmitted: String,
    val stores: List<String>?,
    val partialComplete: PartialCompleteEnum,
    val origin: OriginEnum? = null,
    val products: List<ExtraOrderProductModel>? = null,
)
