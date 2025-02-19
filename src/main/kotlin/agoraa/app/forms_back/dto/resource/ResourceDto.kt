package agoraa.app.forms_back.dto.resource

import agoraa.app.forms_back.dto.resource_products.ResourceProductsDto
import agoraa.app.forms_back.dto.user.UserDto
import agoraa.app.forms_back.enum.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResourceDto(
    val id: Long,
    val user: UserDto,
    val store: StoresEnum,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val orderNumber: Long? = null,
    var products: List<ResourceProductsDto>? = null
)
