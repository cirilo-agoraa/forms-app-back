package agoraa.app.forms_back.service

import agoraa.app.forms_back.model.TokenBlacklistModel
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.repository.TokenBlacklistRepository
import org.springframework.stereotype.Service

@Service
class TokenBlacklistService(
    private val tokenBlacklistRepository: TokenBlacklistRepository
) {

    fun isTokenBlacklisted(refreshToken: String): Boolean {
        return tokenBlacklistRepository.existsByToken(refreshToken)
    }

    fun blacklistToken(refreshToken: String, user: UserModel): TokenBlacklistModel {
        val tokenBlacklist = TokenBlacklistModel(
            token = refreshToken,
            user = user
        )

        return tokenBlacklistRepository.save(tokenBlacklist)
    }
}