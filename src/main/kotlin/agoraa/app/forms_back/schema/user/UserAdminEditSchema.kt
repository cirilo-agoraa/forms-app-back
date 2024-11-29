package agoraa.app.forms_back.schema.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserAdminEditSchema(
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Username must be a valid email address"
    )
    val username: String? = null,

    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$",
        message = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character"
    )
    val password: String? = null,

    val enabled: Boolean? = null,

    val firstAccess: Boolean? = null,
)
