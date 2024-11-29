package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.exceptions.NotAllowedException
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.schema.extra_order.ExtraOrderCreateSchema
import agoraa.app.forms_back.schema.extra_order.ExtraOrderEditSchema
import agoraa.app.forms_back.service.ExtraOrderService
import jakarta.validation.Valid
import org.springdoc.api.OpenApiResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/extra-orders")
class ExtraOrderController(private val extraOrderService: ExtraOrderService) {

    @GetMapping
    fun getAllExtraOrders(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "0") supplierId: Long,
        @RequestParam(defaultValue = "0") userId: Long,
        @RequestParam(defaultValue = "") processed: String,
        @RequestParam(defaultValue = "") dateSubmitted: String,
        @RequestParam(defaultValue = "") partialComplete: String,
        @RequestParam(defaultValue = "") origin: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(
                extraOrderService.findAll(
                    customUserDetails,
                    supplierId,
                    userId,
                    processed,
                    dateSubmitted,
                    origin,
                    partialComplete,
                    page,
                    size,
                    sort,
                    direction
                )
            )
        } catch (e: NotAllowedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @GetMapping("/{id}")
    fun getExtraOrderById(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(extraOrderService.findById(customUserDetails, id))
        } catch (e: NotAllowedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("/{id}/edit")
    fun editExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long,
        @RequestBody @Valid request: ExtraOrderEditSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return try {
            ResponseEntity.ok(extraOrderService.edit(customUserDetails, id, request))
        } catch (e: NotAllowedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }

    @PostMapping
    fun createExtraOrder(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Valid request: ExtraOrderCreateSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return try {
            val currentUser = customUserDetails.getUserModel()
            return ResponseEntity.status(HttpStatus.CREATED).body(extraOrderService.create(currentUser, request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}