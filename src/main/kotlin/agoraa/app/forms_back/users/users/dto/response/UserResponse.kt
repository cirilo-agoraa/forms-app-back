package agoraa.app.forms_back.users.users.dto.response

import agoraa.app.forms_back.enums.RolesEnum
import agoraa.app.forms_back.enums.StoresEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class UserResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val firstAccess: Boolean,
    val store: StoresEnum,
    val enabled: Boolean,
    var roles: List<RolesEnum>? = null
)