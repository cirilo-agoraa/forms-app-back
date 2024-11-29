package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductCreateSchema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ExtraOrderCreateSchema(
    @field:NotNull
    val supplierId: Long,

    @field:NotNull
    val userId: Long,

    @field:NotBlank
    @field:Pattern(regexp = "PARCIAL|COMPLETO", message = "Invalid ExtraOrder Type")
    val partialComplete: String,

    @field:Pattern(regexp = "EXTRA|RECEBIMENTO", message = "Invalid Origin Type")
    val origin: String? = null,

    val storesComplete: List<String>? = null,

    @field:Pattern(regexp = "TRESMANN_SMJ|TRESMANN_VIX|TRESMANN_STT|MERCAPP", message = "Invalid Store Type")
    val storePartial: String? = null,

    val productsInfo: List<ExtraOrderProductCreateSchema>
)
