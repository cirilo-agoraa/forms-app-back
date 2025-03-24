package agoraa.app.forms_back.dto.extra_transfers

import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ExtraTransferDto(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val originStore: StoresEnum,
    val destinyStore: StoresEnum,
    var products: List<ExtraTransferProductsDto>? = null
)
