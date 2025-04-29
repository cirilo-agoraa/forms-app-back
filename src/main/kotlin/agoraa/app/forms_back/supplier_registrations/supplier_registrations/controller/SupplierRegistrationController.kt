package agoraa.app.forms_back.supplier_registrations.supplier_registrations.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.shared.enums.suppliers_registration.SuppliersRegistrationTypesEnum
import agoraa.app.forms_back.supplier_registrations.supplier_registrations.dto.request.SupplierRegistrationCreateSchema
import agoraa.app.forms_back.supplier_registrations.supplier_registrations.dto.request.SupplierRegistrationEditSchema
import agoraa.app.forms_back.supplier_registrations.supplier_registrations.dto.request.SupplierRegistrationPatchRequest
import agoraa.app.forms_back.supplier_registrations.supplier_registrations.service.SupplierRegistrationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/supplier-registrations")
class SupplierRegistrationController(private val supplierRegistrationService: SupplierRegistrationService) {

    @GetMapping
    fun getAllSupplierRegistrations(
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) accepted: Boolean?,
        @RequestParam(required = false) created: Boolean?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierRegistrationService.getAll(
                pagination,
                page,
                size,
                sort,
                direction,
                username,
                createdAt,
                accepted,
                created
            )
        )
    }

    @GetMapping("/current-user")
    fun getCurrentUserSupplierRegistration(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?,
        @RequestParam(required = false) type: SuppliersRegistrationTypesEnum?,
        @RequestParam(required = false) cnpj: String?,
        @RequestParam(required = false) companyName: String?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierRegistrationService.getAllByCurrentUser(
                customUserDetails,
                pagination,
                page,
                size,
                sort,
                direction,
                createdAt,
                processed,
                type,
                cnpj
            )
        )
    }

    @GetMapping("/{id}")
    fun getSupplierRegistration(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierRegistrationService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createSupplierRegistration(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: SupplierRegistrationCreateSchema,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(
                    supplierRegistrationService.create(
                        customUserDetails,
                        request
                    )
                )
            }
        }
    }

    @PutMapping("/{id}/edit")
    fun editSupplierRegistration(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: SupplierRegistrationEditSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierRegistrationService.edit(
                customUserDetails,
                id,
                request
            )
        )
    }

    @PatchMapping("/{id}/patch")
    fun patchSupplierRegistration(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: SupplierRegistrationPatchRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierRegistrationService.patch(
                customUserDetails,
                id,
                request
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteSupplierRegistration(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierRegistrationService.delete(
                customUserDetails,
                id,
            )
        )
    }
}