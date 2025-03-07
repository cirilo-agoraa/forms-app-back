package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.schema.resources.ResourceCreateSchema
import agoraa.app.forms_back.schema.resources.ResourceEditSchema
import agoraa.app.forms_back.service.resources.ResourceService
import jakarta.validation.Valid
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
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) stores: List<StoresEnum>?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) maxDate: LocalDateTime?,
        @RequestParam(required = false) minDate: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.getAll(
                full,
                pagination,
                page,
                size,
                sort,
                direction,
                username,
                stores,
                createdAt,
                maxDate,
                minDate,
                processed,
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
        @RequestParam(required = false) stores: List<StoresEnum>?,
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
                stores,
                createdAt,
                processed
            )
        )
    }

    @GetMapping("/{id}")
    fun getResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: ResourceCreateSchema
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

    @DeleteMapping("/{id}")
    fun deleteResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceService.delete(
                customUserDetails,
                id,
            )
        )
    }
}