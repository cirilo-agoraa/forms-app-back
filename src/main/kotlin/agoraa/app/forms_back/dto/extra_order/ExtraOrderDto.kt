package agoraa.app.forms_back.dto.extra_order

import agoraa.app.forms_back.dto.user.UserDto
import agoraa.app.forms_back.enum.extra_order.OriginEnum
import agoraa.app.forms_back.enum.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.model.suppliers.SupplierModel
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExtraOrderDto(
    val id: Long,
    val user: UserDto,
    val supplier: SupplierModel,
    val processed: Boolean,
    val createdAt: LocalDateTime,
    val partialComplete: PartialCompleteEnum,
    val origin: OriginEnum? = null,
    var stores: List<ExtraOrderStoresDto>? = null,
    var products: List<ExtraOrderProductsDto>? = null,
)
