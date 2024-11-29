package agoraa.app.forms_back.service

import agoraa.app.forms_back.enums.authority.AuthorityTypeEnum
import agoraa.app.forms_back.model.AuthorityModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.AuthorityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class AuthorityService(
    private val authorityRepository: AuthorityRepository
) {

    fun create(user: UserModel, role: String): AuthorityModel {
        return authorityRepository.save(
            AuthorityModel(
                authority = AuthorityTypeEnum.valueOf(role),
                userId = user
            )
        )
    }

    fun findAll(page: Int, size: Int, sort: String, direction: String): Page<AuthorityModel> {
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
        return authorityRepository.findAll(pageable)
    }
}
