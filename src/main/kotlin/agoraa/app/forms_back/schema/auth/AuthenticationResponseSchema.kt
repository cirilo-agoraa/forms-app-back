package agoraa.app.forms_back.schema.auth

data class AuthenticationResponseSchema(
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val authorities: Collection<*>
)