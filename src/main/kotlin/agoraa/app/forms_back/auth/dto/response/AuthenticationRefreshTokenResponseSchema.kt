package agoraa.app.forms_back.auth.dto.response

data class AuthenticationRefreshTokenResponseSchema(
    val accessToken: String,
    val refreshToken: String
)
