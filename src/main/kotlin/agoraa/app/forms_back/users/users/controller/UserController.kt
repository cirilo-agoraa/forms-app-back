package agoraa.app.forms_back.users.users.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.users.users.dto.request.ChangePasswordRequest
import agoraa.app.forms_back.users.users.dto.request.UserRequest
import agoraa.app.forms_back.users.users.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun getUserById(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(userService.getById(customUserDetails, id))

    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "false") full: Boolean,
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam username: String?,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(userService.findAll(full, pagination, username, page, size, sort, direction))

    @PostMapping
    fun createUser(
        @RequestBody @Valid request: UserRequest,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request))
        }
    }

    @PutMapping("/{id}/edit")
    fun editUser(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody @Valid request: UserRequest,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.OK).body(userService.edit(customUserDetails, id, request))
        }
    }

    @PatchMapping("/{id}/change-password")
    fun changeUserPassword(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody @Valid request: ChangePasswordRequest,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(customUserDetails, id, request))
        }
    }
}