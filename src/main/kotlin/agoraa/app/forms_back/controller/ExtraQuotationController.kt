package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.schema.extra_order.ExtraOrderCreateSchema
import agoraa.app.forms_back.schema.extra_order.ExtraOrderEditSchema
import agoraa.app.forms_back.schema.extra_quotations.ExtraQuotationCreateSchema
import agoraa.app.forms_back.schema.extra_quotations.ExtraQuotationEditSchema
import agoraa.app.forms_back.service.extra_quotations.ExtraQuotationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/extra-quotations")

class ExtraQuotationController(private val extraQuotationService: ExtraQuotationService) {
    @GetMapping
    fun getExtraQuotations(
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
            extraQuotationService.getAll(
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
    fun getCurrentUserExtraQuotations(
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
            extraQuotationService.getAllByCurrentUser(
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
    fun getExtraQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraQuotationService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createExtraQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody request: ExtraQuotationCreateSchema,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(
                    extraQuotationService.create(
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
        @RequestBody request: ExtraQuotationEditSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraQuotationService.edit(
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
            extraQuotationService.delete(
                customUserDetails,
                id,
            )
        )
    }
}