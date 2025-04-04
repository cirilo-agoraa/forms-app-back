package agoraa.app.forms_back.extra_orders.extra_orders.dto.request

import agoraa.app.forms_back.extra_orders.extra_order_products.dto.request.ExtraOrderProductRequest
import agoraa.app.forms_back.extra_orders.extra_order_stores.dto.request.ExtraOrderStoresRequest
import agoraa.app.forms_back.extra_orders.extra_orders.enums.OriginEnum
import agoraa.app.forms_back.extra_orders.extra_orders.enums.PartialCompleteEnum
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ExtraOrderRequest(
    @field:NotNull(message = "Supplier cannot be null")
    val supplier: SupplierModel,

    @field:NotNull
    val partialComplete: PartialCompleteEnum,

    @field:NotNull(message = "Stores cannot be null")
    @field:Size(min = 1, message = "At least one store must be selected")
    val stores: List<ExtraOrderStoresRequest>,

    val origin: OriginEnum? = null,
    val products: List<ExtraOrderProductRequest>? = null,
    val processed: Boolean = false
)
