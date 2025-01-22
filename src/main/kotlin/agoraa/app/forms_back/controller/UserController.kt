package agoraa.app.forms_back.controller

import agoraa.app.forms_back.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import agoraa.app.forms_back.schema.user.UserEditSchema
import agoraa.app.forms_back.schema.user.UserCreateSchema

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: Long
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(userService.findById(id))

    @PutMapping("/{id}/edit")
    fun editUser(
        @PathVariable id: Long,
        @RequestBody @Valid request: UserEditSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.OK).body(userService.edit(id, request))
        }
    }

    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam username: String?,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(userService.findAll(pagination, username, page, size, sort, direction))

    @PostMapping
    fun createUser(@RequestBody @Valid request: UserCreateSchema, bindingResult: BindingResult): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request))
        }
    }
}