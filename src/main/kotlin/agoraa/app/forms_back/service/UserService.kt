package agoraa.app.forms_back.service

import agoraa.app.forms_back.dto.user.UserDto
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.UserRepository
import agoraa.app.forms_back.schema.user.UserCreateSchema
import agoraa.app.forms_back.schema.user.UserEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    @Lazy private val encode: BCryptPasswordEncoder,
    private val userRepository: UserRepository,
    private val authorityService: AuthorityService
) {
    private fun createCriteria(username: String?): Specification<UserModel> {
        return Specification { root: Root<UserModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            username?.let {
                predicates.add(criteriaBuilder.like(root.get("username"), "%$it%"))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(user: UserModel, full: Boolean = false): UserDto {
        val userDto = UserDto(
            id = user.id,
            username = user.username,
            store = user.store,
            enabled = user.enabled,
        )

        return when {
            full -> {
                val authorities = authorityService.findByUserId(user.id)
                userDto.authorities = authorities.map { it.authority }
                userDto
            }

            else -> userDto
        }
    }

    fun findAll(pagination: Boolean, username: String?, page: Int, size: Int, sort: String, direction: String): Any {
        val spec = createCriteria(username)
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return if (pagination) {
            val pageable = PageRequest.of(page, size, sortBy)
            userRepository.findAll(spec, pageable)
        } else {
            userRepository.findAll(spec, sortBy)
        }
    }

    fun findById(id: Long): UserModel {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not Found") }
    }

    // used in the jwt auth filter
    fun findByUsername(username: String): UserModel {
        return userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not Found") }
    }

    @Transactional
    fun create(request: UserCreateSchema) {
        val createdUser = userRepository.saveAndFlush(
            UserModel(
                username = request.username,
                password = encode.encode(request.password),
                store = StoresEnum.valueOf(request.store),
            )
        )

        authorityService.create(createdUser, request.roles)
    }

    fun edit(id: Long, request: UserEditSchema) {
        val user = findById(id)

        val editedUser = userRepository.saveAndFlush(
            user.copy(
                enabled = request.enabled ?: user.enabled,
                password = request.password?.let { encode.encode(it) } ?: user.password,
                store = request.store?.let { StoresEnum.valueOf(it) } ?: user.store,
            )
        )

        request.roles?.let { authorityService.edit(editedUser, it) }
    }
}