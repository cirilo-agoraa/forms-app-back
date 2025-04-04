package agoraa.app.forms_back.resources.resources.dto.response

import agoraa.app.forms_back.resources.resource_products.dto.response.ResourceProductsResponse
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ResourceResponse(
    val id: Long,
    val user: UserResponse,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val orderNumber: Long? = null,
    var products: List<ResourceProductsResponse>? = null
)
