package agoraa.app.forms_back.users.users.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class UserResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val firstAccess: Boolean,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    val enabled: Boolean,
    var roles: List<agoraa.app.forms_back.shared.enums.RolesEnum>? = null
)