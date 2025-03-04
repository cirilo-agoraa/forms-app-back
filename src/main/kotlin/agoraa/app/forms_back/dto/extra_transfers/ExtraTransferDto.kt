package agoraa.app.forms_back.dto.extra_transfers

import agoraa.app.forms_back.dto.user.UserDto
import agoraa.app.forms_back.enum.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExtraTransferDto(
    val id: Long,
    val user: UserDto,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val originStore: StoresEnum,
    val destinyStore: StoresEnum,
    var products: List<ExtraTransferProductsDto>? = null
)
