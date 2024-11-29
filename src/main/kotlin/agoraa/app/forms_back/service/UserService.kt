package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.UserRepository
import agoraa.app.forms_back.schema.user.UserAdminEditSchema
import agoraa.app.forms_back.schema.user.UserCreateSchema
import agoraa.app.forms_back.schema.user.UserEditSchema
import jakarta.transaction.Transactional
import org.springdoc.api.OpenApiResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import java.util.*

@Service
class UserService(
    @Lazy private val encode: BCryptPasswordEncoder,
    private val userRepository: UserRepository,
    private val authorityService: AuthorityService
) {
    fun findByUsername(username: String): Optional<UserModel> {
        return userRepository.findByUsername(username)
    }

    fun findById(id: Long): UserModel {
        return userRepository.findById(id)
            .orElseThrow { throw ResourceNotFoundException("User not Found") }
    }

    fun findAll(page: Int, size: Int, sort: String, direction: String): Page<UserModel> {
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
        return userRepository.findAll(pageable)
    }

    fun editAdmin(id: Long, request: UserAdminEditSchema): UserModel {
        val user = findById(id)
        val updatedUser = user.copy(
            username = request.username ?: user.username,
            password = encode.encode(request.password) ?: user.password,
            enabled = request.enabled ?: user.enabled,
            firstAccess = request.firstAccess ?: user.firstAccess
        )
        return userRepository.save(updatedUser)
    }

    fun editCurrentUser(currentUser: UserModel, request: UserEditSchema): UserModel {
        val editedUser = findById(currentUser.id).copy(
            password = request.password?.let { encode.encode(it) } ?: currentUser.password
        )

        return userRepository.save(editedUser)
    }

    @Transactional
    fun create(request: UserCreateSchema): UserModel {
        val user = UserModel(
            username = request.username,
            password = encode.encode(request.password)
        )

        val savedUser = userRepository.saveAndFlush(user)
        authorityService.create(savedUser, request.role)

        return user
    }
}