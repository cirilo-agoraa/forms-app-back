package agoraa.app.forms_back.service

import agoraa.app.forms_back.enums.authority.AuthorityTypeEnum
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.AuthorityModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.AuthorityRepository
import org.springframework.stereotype.Service

@Service
class AuthorityService(
    private val authorityRepository: AuthorityRepository
) {

    fun create(user: UserModel, authorities: List<String>): List<AuthorityModel> {
        val roles = authorities.map { role ->
            AuthorityModel(
                authority = AuthorityTypeEnum.valueOf(role),
                user = user
            )
        }
        return roles
    }

    fun edit(user: UserModel, authorities: List<String>): MutableList<AuthorityModel> {
        val currentRolesSet = user.authorities.map { it.authority.name }.toSet()
        val newRolesSet = authorities.toSet()

        val rolesToDelete = user.authorities.filter { it.authority.name !in newRolesSet }
        deleteAll(rolesToDelete)
        user.authorities.removeAll(rolesToDelete)

        val rolesToAdd = authorities.filter { it !in currentRolesSet }
        val newRoles = rolesToAdd.map { role ->
            AuthorityModel(
                authority = AuthorityTypeEnum.valueOf(role),
                user = user
            )
        }
        user.authorities.addAll(newRoles)
        return user.authorities
    }

    fun delete(authority: AuthorityModel){
        authorityRepository.findById(authority.id)
            .map { authorityRepository.delete(it) }
            .orElseThrow { throw ResourceNotFoundException("Authority not found") }

    }

    fun deleteAll(authorities: List<AuthorityModel>){
        authorities.forEach { delete(it) }
    }
}
