package agoraa.app.forms_back.dto.resource_mips

import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ResourceMipDto(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    var products: List<ResourceMipProductsDto>? = null,
)
