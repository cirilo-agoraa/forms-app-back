package agoraa.app.forms_back.schema.auth

data class AuthenticationRefreshTokenResponseSchema(
    val accessToken: String,
    val refreshToken: String
)
