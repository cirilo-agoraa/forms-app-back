package agoraa.app.forms_back.users.user_roles.repository

import agoraa.app.forms_back.users.user_roles.model.AuthorityModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<AuthorityModel, Long> {
    fun findByUserId(userId: Long): List<AuthorityModel>
}