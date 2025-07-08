package agoraa.app.forms_back.extra_orders.extra_orders.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.extra_orders.extra_orders.dto.request.ExtraOrderPatchRequest
import agoraa.app.forms_back.extra_orders.extra_orders.dto.request.ExtraOrderRequest
import agoraa.app.forms_back.extra_orders.extra_orders.enums.OriginEnum
import agoraa.app.forms_back.extra_orders.extra_orders.enums.PartialCompleteEnum
import agoraa.app.forms_back.extra_orders.extra_orders.service.ExtraOrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/extra-orders")
class ExtraOrderController(private val extraOrderService: ExtraOrderService) {

    @GetMapping
    fun getExtraOrders(
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?,
        @RequestParam(required = false) partialComplete: PartialCompleteEnum?,
        @RequestParam(required = false) origin: OriginEnum?,
        @RequestParam(required = false) supplierName: String? = null,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraOrderService.getAll(
                full,
                pagination,
                page,
                size,
                sort,
                direction,
                username,
                createdAt,
                processed,
                partialComplete,
                origin,
                supplierName
            )
        )
    }

    @GetMapping("/current-user")
    fun getCurrentUserExtraOrders(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) createdAt: LocalDateTime?,
        @RequestParam(required = false) processed: Boolean?,
        @RequestParam(required = false) partialComplete: PartialCompleteEnum?,
        @RequestParam(required = false) origin: OriginEnum?,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraOrderService.getAllByCurrentUser(
                customUserDetails,
                full,
                pagination,
                page,
                size,
                sort,
                direction,
                createdAt,
                processed,
                partialComplete,
                origin
            )
        )
    }

    @GetMapping("/{id}")
    fun getExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraOrderService.getById(
                customUserDetails,
                id,
                full
            )
        )
    }

    @PostMapping
    fun createExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: ExtraOrderRequest,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(
                    extraOrderService.create(
                        customUserDetails,
                        request
                    )
                )
            }
        }
    }

    @PutMapping("/{id}/edit")
    fun editExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: ExtraOrderRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraOrderService.edit(
                customUserDetails,
                id,
                request
            )
        )
    }

    @PatchMapping("/{id}/patch")
    fun editExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody request: ExtraOrderPatchRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraOrderService.patch(
                customUserDetails,
                id,
                request
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            extraOrderService.delete(
                customUserDetails,
                id,
            )
        )
    }
}