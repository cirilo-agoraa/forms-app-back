package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enums.authority.AuthorityTypeEnum
import agoraa.app.forms_back.exceptions.NotAllowedException
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.UserRepository
import agoraa.app.forms_back.schema.user.UserEditSchema
import agoraa.app.forms_back.schema.user.UserCreateSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.domain.Specification

@Service
class UserService(
    @Lazy private val encode: BCryptPasswordEncoder,
    private val userRepository: UserRepository,
    private val authorityService: AuthorityService
) {
    private fun createCriteria(username: String?): Specification<UserModel> {
        return Specification { root: Root<UserModel>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            username?.let {
                predicates.add(criteriaBuilder.like(root.get("username"), "%$it%"))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun findAll(pagination: Boolean, username: String?, page: Int, size: Int, sort: String, direction: String): Any {
        val spec = createCriteria(username)

        if (pagination) {
            val sortDirection =
                if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
            val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
            return userRepository.findAll(spec, pageable)
        } else {
            return userRepository.findAll(spec)
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): UserModel {
        val currentUser = customUserDetails.getUserModel()
        return userRepository.findById(id)
            .map { user ->
                if (currentUser.id == user.id || currentUser.authorities.any { it.authority == AuthorityTypeEnum.ROLE_ADMIN }) {
                    user
                } else {
                    throw NotAllowedException("You are not allowed to access this resource")
                }
            }
            .orElseThrow { ResourceNotFoundException("User not Found") }
    }

    // used in the jwt auth filter
    fun findByUsername(username: String): UserModel {
        return userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not Found") }
    }

    @Transactional
    fun create(request: UserCreateSchema): UserModel {
        val user = UserModel(
            username = request.username,
            password = encode.encode(request.password)
        )

        val roles = authorityService.create(user, request.roles)
        user.authorities.addAll(roles)
        userRepository.save(user)

        return user
    }

    fun edit(customUserDetails: CustomUserDetails, id: Long, request: UserEditSchema): UserModel {
        val currentUser = customUserDetails.getUserModel()
        val existingUser = findById(customUserDetails, id)

        val editedUser = when {
            currentUser.id == existingUser.id -> existingUser.copy(
                password = request.password?.let { encode.encode(it) } ?: existingUser.password,
                firstAccess = false
            )

            currentUser.authorities.any { it.authority == AuthorityTypeEnum.ROLE_ADMIN } -> {
                existingUser.copy(
                    username = request.username ?: existingUser.username,
                    password = request.password?.let { encode.encode(it) } ?: existingUser.password,
                    enabled = request.enabled ?: existingUser.enabled,
                    firstAccess = request.firstAccess ?: existingUser.firstAccess,
                    authorities = request.roles?.let { authorityService.edit(existingUser, it) }
                        ?: existingUser.authorities
                )
            }

            else -> throw ResourceNotFoundException("User not Found")
        }

        return userRepository.save(editedUser)
    }
}