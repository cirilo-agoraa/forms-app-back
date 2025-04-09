package agoraa.app.forms_back.weekly_quotations.weekly_quotations.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.dto.request.WeeklyQuotationRequest
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.service.WeeklyQuotationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/weekly-quotations")
class WeeklyQuotationController(private val weeklyQuotationService: WeeklyQuotationService) {
    @GetMapping
    fun getAllResources(
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) sector: ProductSectorsEnum?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            weeklyQuotationService.getAll(
                full,
                pagination,
                page,
                size,
                sort,
                direction,
                username,
                createdAt,
                sector
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
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) sector: ProductSectorsEnum?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            weeklyQuotationService.getAllByCurrentUser(
                customUserDetails,
                page,
                size,
                sort,
                direction,
                createdAt,
                sector
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
            weeklyQuotationService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: WeeklyQuotationRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            weeklyQuotationService.create(
                customUserDetails,
                request
            )
        )
    }

    @PutMapping("/{id}/edit")
    fun editResource(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: WeeklyQuotationRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            weeklyQuotationService.edit(
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
            weeklyQuotationService.delete(
                customUserDetails,
                id,
            )
        )
    }
}