package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.schema.resource_mips.ResourceMipCreateSchema
import agoraa.app.forms_back.schema.resource_mips.ResourceMipEditSchema
import agoraa.app.forms_back.service.resource_mips.ResourceMipService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/resource-mips")
class ResourceMipController(private val resourceMipService: ResourceMipService) {
    @GetMapping
    fun getResourceMips(
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceMipService.getAll(
                full,
                pagination,
                page,
                size,
                sort,
                direction,
                username,
                createdAt,
                processed,
            )
        )
    }

    @GetMapping("/current-user")
    fun getCurrentUserResourceMips(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceMipService.getAllByCurrentUser(
                customUserDetails,
                pagination,
                page,
                size,
                sort,
                direction,
                createdAt,
                processed,
            )
        )
    }

    @GetMapping("/{id}")
    fun getResourceMip(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceMipService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createExtraQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody request: ResourceMipCreateSchema,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(
                    resourceMipService.create(
                        customUserDetails,
                        request
                    )
                )
            }
        }
    }

    @PutMapping("/{id}/edit")
    fun editExtraQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: ResourceMipEditSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceMipService.edit(
                customUserDetails,
                id,
                request
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteExtraQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            resourceMipService.delete(
                customUserDetails,
                id,
            )
        )
    }
}