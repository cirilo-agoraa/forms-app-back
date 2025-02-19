package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductCreateSchema
import jakarta.validation.constraints.Pattern

data class ExtraOrderEditSchema(
    val supplier: SupplierModel? = null,

    @field:Pattern(regexp = "PARCIAL|COMPLETO", message = "Invalid Extra Order Type")
    val partialComplete: String? = null,

    val processed: Boolean? = null,

    val stores: List<String>? = null,

    val products: List<ExtraOrderProductCreateSchema>? = null,

    @field:Pattern(regexp = "EXTRA|RECEBIMENTO", message = "Invalid Origin Type")
    val origin: String? = null,

    )
