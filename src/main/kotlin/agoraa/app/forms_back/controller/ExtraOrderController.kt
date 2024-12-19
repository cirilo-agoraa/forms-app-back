package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enums.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.exceptions.NotAllowedException
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.schema.extra_order.ExtraOrderCreateSchema
import agoraa.app.forms_back.schema.extra_order.ExtraOrderEditSchema
import agoraa.app.forms_back.service.ExtraOrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/extra-orders")
class ExtraOrderController(private val extraOrderService: ExtraOrderService) {

    @GetMapping
    fun getAllExtraOrders(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam supplier: Long?,
        @RequestParam user: Long?,
        @RequestParam processed: Boolean?,
        @RequestParam dateSubmitted: String?,
        @RequestParam partialComplete: PartialCompleteEnum?,
        @RequestParam origin: String?,
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            extraOrderService.findAll(
                customUserDetails,
                pagination,
                supplier,
                user,
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
    }

    @GetMapping("/{id}")
    fun getExtraOrderById(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable id: Long
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(extraOrderService.returnById(customUserDetails, id))
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
        return ResponseEntity.status(HttpStatus.OK).body(extraOrderService.edit(customUserDetails, id, request))
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
        return ResponseEntity.status(HttpStatus.CREATED).body(extraOrderService.create(customUserDetails, request))
    }
}