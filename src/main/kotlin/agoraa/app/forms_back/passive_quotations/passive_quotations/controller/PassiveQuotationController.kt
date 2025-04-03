package agoraa.app.forms_back.passive_quotations.passive_quotations.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationCalculateRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationPrintRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request.PassiveQuotationRequest
import agoraa.app.forms_back.passive_quotations.passive_quotations.service.PassiveQuotationService
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
    fun getAllResources(
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) supplier: String?,
        @RequestParam(required = false) store: agoraa.app.forms_back.shared.enums.StoresEnum?,
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
    fun getCurrentUserResources(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) supplier: String?,
        @RequestParam(required = false) store: agoraa.app.forms_back.shared.enums.StoresEnum?,
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
    fun getResource(
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
    fun createResource(
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
    fun editResource(
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

    @DeleteMapping("/{id}")
    fun deleteResource(
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