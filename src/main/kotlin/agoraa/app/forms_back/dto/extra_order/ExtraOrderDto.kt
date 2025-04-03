package agoraa.app.forms_back.dto.extra_order

import agoraa.app.forms_back.shared.enums.extra_order.OriginEnum
import agoraa.app.forms_back.shared.enums.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ExtraOrderDto(
    val id: Long,
    val user: UserResponse,
    val supplier: SupplierModel,
    val processed: Boolean,
    val createdAt: LocalDateTime,
    val partialComplete: agoraa.app.forms_back.shared.enums.extra_order.PartialCompleteEnum,
    val origin: agoraa.app.forms_back.shared.enums.extra_order.OriginEnum? = null,
    var stores: List<ExtraOrderStoresDto>? = null,
    var products: List<ExtraOrderProductsDto>? = null
)
