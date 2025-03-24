package agoraa.app.forms_back.schema.extra_transfers

import agoraa.app.forms_back.enums.StoresEnum
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ExtraTransferCreateSchema(
    @field:NotNull(message = "Products are required")
    @field:Size(min = 1)
    val products: List<ExtraTransferProductsCreateSchema>,

    @field:NotNull(message = "Origin Store cannot be null")
    val originStore: StoresEnum,

    @field:NotNull(message = "Destiny Store cannot be null")
    val destinyStore: StoresEnum,
)