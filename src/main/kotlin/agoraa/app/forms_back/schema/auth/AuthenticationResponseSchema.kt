package agoraa.app.forms_back.schema.auth

import agoraa.app.forms_back.enum.StoresEnum

data class AuthenticationResponseSchema(
    val username: String,
    val store: StoresEnum,
    val accessToken: String,
    val refreshToken: String,
    val authorities: Collection<*>
)