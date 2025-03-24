package agoraa.app.forms_back.users.users.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.users.user_roles.service.AuthorityService
import agoraa.app.forms_back.users.users.dto.request.ChangePasswordRequest
import agoraa.app.forms_back.users.users.dto.request.UserCreateRequest
import agoraa.app.forms_back.users.users.dto.request.UserEditRequest
import agoraa.app.forms_back.users.users.dto.response.UserResponse
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.users.users.repository.UserRepository
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.PageImpl
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
    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        user: UserModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isSameUser = user.id == currentUser.id

        return isAdmin || isSameUser
    }

    private fun createCriteria(username: String?): Specification<UserModel> {
        return Specification { root: Root<UserModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            username?.let {
                predicates.add(criteriaBuilder.like(root.get("username"), "%$it%"))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(user: UserModel, full: Boolean = false): UserResponse {
        val userResponseSchema = UserResponse(
            id = user.id,
            username = user.username,
            store = user.store,
            enabled = user.enabled,
            nickname = user.nickname,
            firstAccess = user.firstAccess,
        )

        return when {
            full -> {
                val roles = authorityService.findByParentId(user.id)
                userResponseSchema.roles = roles.map { it.authority }
                userResponseSchema
            }

            else -> userResponseSchema
        }
    }

    fun findAll(
        full: Boolean,
        pagination: Boolean,
        username: String?,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val spec = createCriteria(username)
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = userRepository.findAll(spec, pageable)

                return PageImpl(pageResult.content.map { createDto(it, full) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraOrders = userRepository.findAll(spec, sortBy)

                extraOrders.map { createDto(it, full) }
            }
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): UserModel {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        return when {
            hasPermission(customUserDetails, user) -> user
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(customUserDetails: CustomUserDetails, id: Long): UserResponse {
        val user = findById(customUserDetails, id)
        return createDto(user, true)
    }

    // used in the jwt auth filter
    fun findByUsername(username: String): UserModel {
        return userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not Found") }
    }

    @Transactional
    fun create(request: UserCreateRequest) {
        val createdUser = userRepository.saveAndFlush(
            UserModel(
                username = request.username,
                password = encode.encode(request.password),
                store = request.store,
                nickname = request.nickname,
                firstAccess = request.firstAccess,
                enabled = request.enabled
            )
        )

        authorityService.createOrDelete(createdUser, request.roles)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: UserEditRequest) {
        val user = findById(customUserDetails, id)

        val editedUser = userRepository.saveAndFlush(
            user.copy(
                username = request.username,
                store = request.store,
                nickname = request.nickname,
                firstAccess = request.firstAccess,
                enabled = request.enabled
            )
        )

        authorityService.createOrDelete(editedUser, request.roles)
    }

    fun changePassword(customUserDetails: CustomUserDetails, id: Long, request: ChangePasswordRequest) {
        val user = findById(customUserDetails, id)

        userRepository.saveAndFlush(
            user.copy(
                password = encode.encode(request.password)
            )
        )
    }
}
