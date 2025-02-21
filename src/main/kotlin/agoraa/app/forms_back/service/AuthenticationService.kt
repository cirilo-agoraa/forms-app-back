package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.JwtProperties
import agoraa.app.forms_back.schema.auth.AuthenticationRefreshTokenRequestSchema
import agoraa.app.forms_back.schema.auth.AuthenticationRefreshTokenResponseSchema
import agoraa.app.forms_back.schema.auth.AuthenticationRequestSchema
import agoraa.app.forms_back.schema.auth.AuthenticationResponseSchema
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val jdbcUserDetailsManager: JdbcUserDetailsManager,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val userService: UserService,
    private val tokenBlacklistService: TokenBlacklistService
) {
    fun authentication(authRequest: AuthenticationRequestSchema): AuthenticationResponseSchema {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.email,
                authRequest.password
            )
        )

        val user = jdbcUserDetailsManager.loadUserByUsername(authRequest.email)
        val accessToken = tokenService.generateAccessToken(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )
        val refreshToken = tokenService.generateRefreshToken(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration),
        )

        return AuthenticationResponseSchema(
            username = user.username,
            accessToken = accessToken,
            refreshToken = refreshToken,
            authorities = user.authorities.map { it.authority }
        )
    }

    fun refreshToken(request: AuthenticationRefreshTokenRequestSchema): AuthenticationRefreshTokenResponseSchema {
        if (!tokenService.isValidRefreshToken(request.refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        val email = tokenService.extractEmail(request.refreshToken)
            ?: throw IllegalArgumentException("Invalid token: Unable to extract user details")

        val foundUser = jdbcUserDetailsManager.loadUserByUsername(email)
            ?: throw IllegalArgumentException("User not found for the provided token")

        val user = userService.findByUsername(email)

        tokenBlacklistService.blacklistToken(request.refreshToken, user)

        val accessToken = tokenService.generateAccessToken(
            userDetails = foundUser,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )

        val refreshToken = tokenService.generateRefreshToken(
            userDetails = foundUser,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
        )

        return AuthenticationRefreshTokenResponseSchema(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}