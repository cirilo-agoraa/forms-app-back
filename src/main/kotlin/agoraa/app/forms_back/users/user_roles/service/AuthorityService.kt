package agoraa.app.forms_back.users.user_roles.service

import agoraa.app.forms_back.enums.RolesEnum
import agoraa.app.forms_back.users.user_roles.model.AuthorityModel
import agoraa.app.forms_back.users.user_roles.repository.AuthorityRepository
import agoraa.app.forms_back.users.users.model.UserModel
import org.springframework.stereotype.Service

@Service
class AuthorityService(
    private val authorityRepository: AuthorityRepository
) {

    private fun create(user: UserModel, roles: List<RolesEnum>) {
        val authorities = roles.map { role ->
           AuthorityModel(
               user = user,
               authority = role
            )
        }
        authorityRepository.saveAll(authorities)
    }

    fun findByParentId(
        userId: Long,
    ): List<AuthorityModel> = authorityRepository.findByUserId(userId)

    fun createOrDelete(user: UserModel, roles: List<RolesEnum>) {
        val authorities = findByParentId(user.id)
        val currentRolesSet = authorities.map { it.authority }.toSet()
        val newRolesSet = roles.toSet()

        val toAdd = roles.filter { it !in currentRolesSet }
        create(user, toAdd)

        val toDelete = authorities.filter { it.authority !in newRolesSet }
        authorityRepository.deleteAll(toDelete)
    }
}
