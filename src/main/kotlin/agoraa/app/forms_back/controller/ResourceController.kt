package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.schema.resource.ResourceCreateSchema
import agoraa.app.forms_back.schema.resource.ResourceEditSchema
import agoraa.app.forms_back.service.ResourceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/resources")
class ResourceController(private val resourceService: ResourceService) {

    @GetMapping
    fun getAllResources(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        username: String?,
        store: StoresEnum?,
        createdAt: LocalDateTime?,
        processed: Boolean?
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.getAll(
                page,
                size,
                sort,
                direction,
                username,
                store,
                createdAt,
                processed
            )
        )
    }

    @GetMapping("/current-user")
    fun getCurrentUserResources(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) store: StoresEnum?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.getAllByCurrentUser(
                customUserDetails,
                page,
                size,
                sort,
                direction,
                store,
                createdAt,
                processed
            )
        )
    }

    @GetMapping("/{id}")
    fun getResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.getById(
                customUserDetails,
                id
            )
        )
    }

    @PostMapping
    fun createResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody request: ResourceCreateSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            resourceService.create(
                customUserDetails,
                request
            )
        )
    }

    @PutMapping("/{id}/edit")
    fun editResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: ResourceEditSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.edit(
                customUserDetails,
                id,
                request
            )
        )
    }
}