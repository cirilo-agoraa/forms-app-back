package agoraa.app.forms_back.users.users.dto.request

import agoraa.app.forms_back.shared.enums.RolesEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserEditRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Username must be a valid email address"
    )
    val username: String,

    @field:NotBlank(message = "Nickname cannot be blank")
    val nickname: String,

    @field:NotNull(message = "At least one role must be selected")
    @field:Size(min = 1, message = "At least one role must be selected")
    val roles: List<agoraa.app.forms_back.shared.enums.RolesEnum>,

    @field:NotNull(message = "Store cannot be null")
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,

    val enabled: Boolean = true,
    val firstAccess: Boolean = true
)
