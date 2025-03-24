package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.enums.StoresEnum
import jakarta.validation.constraints.NotNull

data class ExtraOrderStoresCreateSchema(
    @field:NotNull(message = "Stores cannot be null")
    val store: StoresEnum
)
