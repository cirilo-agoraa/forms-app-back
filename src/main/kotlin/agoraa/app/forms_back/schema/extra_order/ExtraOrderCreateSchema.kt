package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.model.suppliers.SupplierModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class ExtraOrderCreateSchema(
    @field:NotNull(message = "Supplier cannot be null")
    val supplier: SupplierModel,

    @field:NotBlank
    @field:Pattern(regexp = "PARCIAL|COMPLETO", message = "Invalid ExtraOrder Type")
    val partialComplete: String,

    @field:NotNull(message = "Stores cannot be null")
    val stores: List<ExtraOrderStoresCreateSchema>,

    val origin: String? = null,
    val products: List<ExtraOrderProductCreateSchema>? = null,
)
