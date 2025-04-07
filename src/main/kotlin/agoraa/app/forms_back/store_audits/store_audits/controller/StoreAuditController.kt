package agoraa.app.forms_back.store_audits.store_audits.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.shared.enums.ProductGroupsEnum
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.store_audits.store_audits.dto.request.StoreAuditPatchRequest
import agoraa.app.forms_back.store_audits.store_audits.dto.request.StoreAuditRequest
import agoraa.app.forms_back.store_audits.store_audits.service.StoreAuditService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/store-audits")
class StoreAuditController(private val storeAuditService: StoreAuditService) {
    @GetMapping
    fun getExtraTransfers(
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
            storeAuditService.getAll(
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
    fun getCurrentUserExtraTransfers(
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
            storeAuditService.getAllByCurrentUser(
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
    fun getExtraTransfer(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            storeAuditService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createExtraTransfer(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: StoreAuditRequest,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(
                    storeAuditService.create(
                        customUserDetails,
                        request
                    )
                )
            }
        }
    }

    @PutMapping("/{id}/edit")
    fun editExtraTransfer(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: StoreAuditRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            storeAuditService.edit(
                customUserDetails,
                id,
                request
            )
        )
    }

    @PatchMapping("/{id}/patch")
    fun patchExtraTransfer(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: StoreAuditPatchRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            storeAuditService.patch(
                customUserDetails,
                id,
                request
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteExtraTransfer(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            storeAuditService.delete(
                customUserDetails,
                id,
            )
        )
    }
}