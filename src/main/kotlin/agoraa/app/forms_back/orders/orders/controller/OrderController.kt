package agoraa.app.forms_back.orders.orders.controller

import agoraa.app.forms_back.orders.orders.dto.request.OrderRequest
import agoraa.app.forms_back.orders.orders.service.OrderService
import agoraa.app.forms_back.shared.enums.StoresEnum
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {
    @GetMapping
    fun getAllOrders(
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) dateCreated: LocalDateTime?,
        @RequestParam(required = false) orderNumber: Long?,
        @RequestParam(required = false) store: StoresEnum?,
        @RequestParam(required = false) issued: Boolean?,
        @RequestParam(required = false) received: Boolean?,
        @RequestParam(required = false) supplier: String?,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(
                orderService.getAll(
                    pagination,
                    page,
                    size,
                    sort,
                    direction,
                    dateCreated,
                    orderNumber,
                    store,
                    issued,
                    received,
                    supplier
                )
            )

    @GetMapping("/current-user")
    fun getOrdersByCurrentUser(
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) dateCreated: LocalDateTime?,
        @RequestParam(required = false) orderNumber: Long?,
        @RequestParam(required = false) store: StoresEnum?,
        @RequestParam(required = false) issued: Boolean?,
        @RequestParam(required = false) received: Boolean?,
        @RequestParam(required = false) supplier: String?
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(
                orderService.getAll(
                    pagination,
                    page,
                    size,
                    sort,
                    direction,
                    dateCreated,
                    orderNumber,
                    store,
                    issued,
                    received,
                    supplier
                )
            )

    @GetMapping("/{id}")
    fun getOrderById(
        @PathVariable id: Long,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(orderService.returnById(id))

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateMultipleOrders(
        @Valid @RequestBody request: List<OrderRequest>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                orderService.editOrCreateMultiple(request)
                ResponseEntity.status(HttpStatus.OK).body("Orders created or edited successfully")
            }
        }
    }
}