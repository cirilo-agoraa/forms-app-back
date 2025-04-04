package agoraa.app.forms_back.extra_orders.extra_orders.dto.response

import agoraa.app.forms_back.extra_orders.extra_order_products.dto.response.ExtraOrderProductsResponse
import agoraa.app.forms_back.extra_orders.extra_order_stores.dto.response.ExtraOrderStoresResponse
import agoraa.app.forms_back.extra_orders.extra_orders.enums.OriginEnum
import agoraa.app.forms_back.extra_orders.extra_orders.enums.PartialCompleteEnum
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ExtraOrderResponse(
    val id: Long,
    val user: UserResponse,
    val supplier: SupplierModel,
    val processed: Boolean,
    val createdAt: LocalDateTime,
    val partialComplete: PartialCompleteEnum,
    val origin: OriginEnum? = null,
    var stores: List<ExtraOrderStoresResponse>? = null,
    var products: List<ExtraOrderProductsResponse>? = null
)
