package agoraa.app.forms_back.auth.dto.request

data class AuthenticationRequestSchema(
    val email: String,
    val password: String
)