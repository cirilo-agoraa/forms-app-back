package agoraa.app.forms_back.dto.resource_mips

import agoraa.app.forms_back.dto.user.UserDto
import agoraa.app.forms_back.enum.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResourceMipDto(
    val id: Long,
    val user: UserDto,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val store: StoresEnum,
    var products: List<ResourceMipProductsDto>? = null,
)
