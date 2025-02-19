package agoraa.app.forms_back.dto.user

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.authority.AuthorityTypeEnum
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val id: Long,
    val username: String,
    val store: StoresEnum,
    val enabled: Boolean,
    var authorities: List<AuthorityTypeEnum>? = null
)
