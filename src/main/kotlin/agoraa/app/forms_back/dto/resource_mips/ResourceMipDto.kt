package agoraa.app.forms_back.dto.resource_mips

import agoraa.app.forms_back.dto.user.UserDto
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResourceMipDto(
    val id: Long,
    val user: UserDto,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    var items: List<ResourceMipItemsDto>? = null,
)
