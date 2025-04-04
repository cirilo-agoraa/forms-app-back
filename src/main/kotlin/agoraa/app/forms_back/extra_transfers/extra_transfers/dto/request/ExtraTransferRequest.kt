package agoraa.app.forms_back.extra_transfers.extra_transfers.dto.request

import agoraa.app.forms_back.extra_transfers.extra_transfer_products.dto.request.ExtraTransferProductsRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ExtraTransferRequest(
    @field:NotNull(message = "Products are required")
    @field:Size(min = 1)
    val products: List<ExtraTransferProductsRequest>,

    @field:NotNull(message = "Origin Store cannot be null")
    val originStore: agoraa.app.forms_back.shared.enums.StoresEnum,

    @field:NotNull(message = "Destiny Store cannot be null")
    val destinyStore: agoraa.app.forms_back.shared.enums.StoresEnum,

    @field:NotBlank(message = "Name is required")
    val name: String,

    val processed: Boolean = false
)