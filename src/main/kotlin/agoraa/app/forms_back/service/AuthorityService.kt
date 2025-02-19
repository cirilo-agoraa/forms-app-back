package agoraa.app.forms_back.service

import agoraa.app.forms_back.enum.authority.AuthorityTypeEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.AuthorityModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.AuthorityRepository
import org.springframework.stereotype.Service

@Service
class AuthorityService(
    private val authorityRepository: AuthorityRepository
) {

    fun findByUserId(userId: Long): List<AuthorityModel> {
        return authorityRepository.findByUserId(userId)
    }

    fun create(user: UserModel, authorities: List<String>) {
        val roles = authorities.map { role ->
            AuthorityModel(
                authority = AuthorityTypeEnum.valueOf(role),
                user = user
            )
        }
        authorityRepository.saveAll(roles)
    }

    fun edit(user: UserModel, authorities: List<String>) {
        val currentRoles = findByUserId(user.id)
        val currentRolesSet = currentRoles.map { it.authority.name }.toSet()
        val newRolesSet = authorities.toSet()

        val rolesToDelete = currentRoles.filter { it.authority.name !in newRolesSet }
        deleteAll(rolesToDelete)

        val rolesToAdd = authorities.filter { it !in currentRolesSet }
        val newRoles = rolesToAdd.map { role ->
            AuthorityModel(
                authority = AuthorityTypeEnum.valueOf(role),
                user = user
            )
        }
        authorityRepository.saveAll(newRoles)
    }

    fun delete(authority: AuthorityModel) {
        authorityRepository.findById(authority.id)
            .map { authorityRepository.delete(it) }
            .orElseThrow { throw ResourceNotFoundException("Authority not found") }

    }

    fun deleteAll(authorities: List<AuthorityModel>) {
        authorities.forEach { delete(it) }
    }
}
