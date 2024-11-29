package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.TokenBlacklistModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenBlacklistRepository: JpaRepository<TokenBlacklistModel, Long> {

    fun existsByToken(token: String): Boolean
}