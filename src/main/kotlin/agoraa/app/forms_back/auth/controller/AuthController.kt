package agoraa.app.forms_back.auth.controller

import agoraa.app.forms_back.auth.dto.request.AuthenticationRefreshTokenRequestSchema
import agoraa.app.forms_back.auth.dto.response.AuthenticationRefreshTokenResponseSchema
import agoraa.app.forms_back.auth.dto.request.AuthenticationRequestSchema
import agoraa.app.forms_back.auth.dto.response.AuthenticationResponseSchema
import agoraa.app.forms_back.auth.service.AuthenticationService
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