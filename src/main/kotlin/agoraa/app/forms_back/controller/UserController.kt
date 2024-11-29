package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.schema.user.UserAdminEditSchema
import agoraa.app.forms_back.schema.user.UserCreateSchema
import agoraa.app.forms_back.schema.user.UserEditSchema
import org.springdoc.api.OpenApiResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.security.core.annotation.AuthenticationPrincipal

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    // ADMIN ENDPOINTS

    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Page<UserModel>> =
        ResponseEntity.status(HttpStatus.OK).body(userService.findAll(page, size, sort, direction))

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(userService.findById(id))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("/{id}/edit")
    fun editUser(
        @PathVariable id: Long,
        @RequestBody @Valid request: UserAdminEditSchema,
        bindingResult: BindingResult
    ): ResponseEntity<out Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return try {
            ResponseEntity.status(HttpStatus.OK).body(userService.editAdmin(id, request))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PostMapping
    fun createUser(@RequestBody @Valid request: UserCreateSchema, bindingResult: BindingResult): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request))
    }

    // USER ENDPOINTS

    @PutMapping("/edit-current")
    fun editCurrentUser(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Valid request: UserEditSchema, bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        val currentUser = customUserDetails.getUserModel()
        return ResponseEntity.status(HttpStatus.OK).body((userService.editCurrentUser(currentUser, request)))
    }
}