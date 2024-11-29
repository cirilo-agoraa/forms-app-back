package agoraa.app.forms_back.controller

import agoraa.app.forms_back.schema.auth.AuthenticationRefreshTokenRequestSchema
import agoraa.app.forms_back.schema.auth.AuthenticationRefreshTokenResponseSchema
import agoraa.app.forms_back.schema.auth.AuthenticationRequestSchema
import agoraa.app.forms_back.schema.auth.AuthenticationResponseSchema
import agoraa.app.forms_back.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthenticationRequestSchema): AuthenticationResponseSchema =
        authenticationService.authentication(authRequest)

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: AuthenticationRefreshTokenRequestSchema): AuthenticationRefreshTokenResponseSchema =
        authenticationService.refreshToken(request)
}