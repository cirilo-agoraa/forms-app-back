package agoraa.app.forms_back.auth.dto.response

data class AuthenticationResponseSchema(
    val id: Long,
    val firstAccess: Boolean,
    val nickname: String,
    val username: String,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum,
    val accessToken: String,
    val refreshToken: String,
    val authorities: Collection<*>
)