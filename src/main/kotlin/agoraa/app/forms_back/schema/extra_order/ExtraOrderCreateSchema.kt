package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductCreateSchema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class ExtraOrderCreateSchema(
    @field:NotNull(message = "Supplier cannot be null")
    val supplierId: Long,

    @field:NotBlank
    @field:Pattern(regexp = "PARCIAL|COMPLETO", message = "Invalid ExtraOrder Type")
    val partialComplete: String,

    @field:NotNull(message = "Stores cannot be null")
    val stores: List<String>,

    val products: List<ExtraOrderProductCreateSchema>? = null,

    @field:Pattern(regexp = "EXTRA|RECEBIMENTO", message = "Invalid Origin Type")
    val origin: String? = null
)
