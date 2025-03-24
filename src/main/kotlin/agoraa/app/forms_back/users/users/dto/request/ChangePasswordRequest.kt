package agoraa.app.forms_back.users.users.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ChangePasswordRequest(
    @field:NotBlank(message = "Password cannot be blank")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$",
        message = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character"
    )
    val password: String,
)
