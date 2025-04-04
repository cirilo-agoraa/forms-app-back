package agoraa.app.forms_back.resource_mips.resource_mips.dto.response

import agoraa.app.forms_back.resource_mips.resource_mip_products.dto.response.ResourceMipProductsResponse
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
data class ResourceMipResponse(
    val id: Long,
    val user: UserResponse,
    val createdAt: LocalDateTime,
    val processed: Boolean,
    val store: StoresEnum,
    var products: List<ResourceMipProductsResponse>? = null,
)
