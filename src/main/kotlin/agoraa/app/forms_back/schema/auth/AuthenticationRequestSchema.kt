package agoraa.app.forms_back.schema.auth

data class AuthenticationRequestSchema(
    val email: String,
    val password: String
)