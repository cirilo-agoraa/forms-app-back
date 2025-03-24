package agoraa.app.forms_back.dto.resource

import agoraa.app.forms_back.dto.resource_products.ResourceProductsDto
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ResourceDto(
    val id: Long,
    val user: UserResponse,
    val store: StoresEnum,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val orderNumber: Long? = null,
    var products: List<ResourceProductsDto>? = null
)
