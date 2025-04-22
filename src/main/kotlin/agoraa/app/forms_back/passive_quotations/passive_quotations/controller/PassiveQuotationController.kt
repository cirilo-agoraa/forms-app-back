package agoraa.app.forms_back.passive_quotations.passive_quotations.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationCalculateRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationPatchRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationPrintRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.service.PassiveQuotationService
import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/passive-quotations")
class PassiveQuotationController(private val passiveQuotationService: PassiveQuotationService) {
    @GetMapping
    fun getAllPassiveQuotations(
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) supplier: String?,
        @RequestParam(required = false) store: StoresEnum?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            passiveQuotationService.getAll(
                full,
                pagination,
                page,
                size,
                sort,
                direction,
                username,
                supplier,
                createdAt,
                store
            )
        )
    }

    @GetMapping("/current-user")
    fun getCurrentUserPassiveQuotations(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) supplier: String?,
        @RequestParam(required = false) store: StoresEnum?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            passiveQuotationService.getAllByCurrentUser(
                customUserDetails,
                page,
                size,
                sort,
                direction,
                supplier,
                createdAt,
                store
            )
        )
    }

    @GetMapping("/{id}")
    fun getPassiveQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "true") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            passiveQuotationService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createPassiveQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: PassiveQuotationRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            passiveQuotationService.create(
                customUserDetails,
                request
            )
        )
    }

    @PutMapping("/{id}/edit")
    fun patchPassiveQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: PassiveQuotationRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            passiveQuotationService.edit(
                customUserDetails,
                id,
                request
            )
        )
    }

    @PatchMapping("/{id}/patch")
    fun patchPassiveQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: PassiveQuotationPatchRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            passiveQuotationService.patch(
                customUserDetails,
                id,
                request
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deletePassiveQuotation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            passiveQuotationService.delete(
                customUserDetails,
                id,
            )
        )
    }

    @PostMapping("/calculate")
    fun calculate(@RequestBody request: PassiveQuotationCalculateRequest) = ResponseEntity.status(HttpStatus.OK).body(
        passiveQuotationService.calculateQuotation(request)
    )

    @PostMapping("/send-pdf")
    fun sendPdf(@RequestBody request: PassiveQuotationPrintRequest) = ResponseEntity.status(HttpStatus.OK).body(
        passiveQuotationService.sendPdf(request)
    )
}