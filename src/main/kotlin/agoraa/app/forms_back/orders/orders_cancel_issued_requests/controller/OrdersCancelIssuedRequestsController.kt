package agoraa.app.forms_back.orders.orders_cancel_issued_requests.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.dto.request.OrdersCancelIssuedRequestsPatchRequest
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.dto.request.OrdersCancelIssuedRequestsRequest
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.service.OrdersCancelIssuedRequestsService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders/cancel-issued-requests")
class OrdersCancelIssuedRequestsController(private val ordersCancelIssuedRequestsService: OrdersCancelIssuedRequestsService) {
    @GetMapping
    fun getAllCancelIssuedRequests(
        @RequestParam(required = false) processed: Boolean?,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(ordersCancelIssuedRequestsService.getAll(processed))

    @PostMapping
    fun createCancelIssuedRequest(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @Valid @RequestBody request: OrdersCancelIssuedRequestsRequest,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(
                    ordersCancelIssuedRequestsService.create(
                        customUserDetails,
                        request
                    )
                )
            }
        }
    }

    @PatchMapping("/{id}/patch")
    fun patchCancelIssuedRequest(
        @PathVariable id: Long,
        @RequestBody request: OrdersCancelIssuedRequestsPatchRequest,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(ordersCancelIssuedRequestsService.patch(id, request))
}