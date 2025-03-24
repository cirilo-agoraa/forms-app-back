package agoraa.app.forms_back.schema.auth

import agoraa.app.forms_back.enums.StoresEnum

data class AuthenticationResponseSchema(
    val nickname: String,
    val username: String,
    val store: StoresEnum,
    val accessToken: String,
    val refreshToken: String,
    val authorities: Collection<*>
)