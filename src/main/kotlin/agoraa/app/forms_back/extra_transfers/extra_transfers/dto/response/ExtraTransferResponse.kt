package agoraa.app.forms_back.extra_transfers.extra_transfers.dto.response

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.extra_transfers.extra_transfer_products.dto.response.ExtraTransferProductsResponse
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ExtraTransferResponse(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val name: String,
    val originStore: agoraa.app.forms_back.shared.enums.StoresEnum,
    val destinyStore: agoraa.app.forms_back.shared.enums.StoresEnum,
    var products: List<ExtraTransferProductsResponse>? = null
)
