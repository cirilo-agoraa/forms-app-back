package agoraa.app.forms_back.users.users.dto.request

import agoraa.app.forms_back.enums.RolesEnum
import agoraa.app.forms_back.enums.StoresEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Username must be a valid email address"
    )
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$",
        message = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character"
    )
    val password: String,

    @field:NotBlank(message = "Nickname cannot be blank")
    val nickname: String,

    @field:NotNull(message = "At least one role must be selected")
    @field:Size(min = 1, message = "At least one role must be selected")
    val roles: List<RolesEnum>,

    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    val enabled: Boolean = true,
    val firstAccess: Boolean = true

)