package agoraa.app.forms_back.schema.extra_order

import jakarta.validation.constraints.Pattern

data class ExtraOrderEditSchema(
    val userId: Long? = null,

    val supplierId: Long? = null,

    @field:Pattern(regexp = "PARCIAL|COMPLETO", message = "Invalid Extra Order Type")
    val partialComplete: String? = null,

    @field:Pattern(regexp = "EXTRA|RECEBIMENTO", message = "Invalid Origin Type")
    val origin: String? = null,

    val storesComplete: List<String>? = null,

    @field:Pattern(regexp = "TRESMANN_SMJ|TRESMANN_VIX|TRESMANN_STT|MERCAPP", message = "Invalid Store Type")
    val storePartial: String? = null,

    val processed: Boolean? = null,

    val dateSubmitted: String? = null
)
