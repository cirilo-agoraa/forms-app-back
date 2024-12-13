package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderProductModel
import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductEditSchema
import agoraa.app.forms_back.service.ExtraOrderProductService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/extra-orders-products")
class ExtraOrderProductController(private val extraOrderProductService: ExtraOrderProductService) {

    @GetMapping
    fun getAllExtraOrderProducts(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "true") pagination: String,
        @RequestParam(defaultValue = "0") extraOrderId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.OK)
                .body(extraOrderProductService.findAll(customUserDetails, pagination, extraOrderId, page, size, sort, direction))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @GetMapping("/{id}")
    fun getExtraOrderProductById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(extraOrderProductService.findById(id))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/{id}/delete")
    fun deleteExtraOrderProduct(@RequestParam id: Long): Any{
        return try {
            ResponseEntity.ok(extraOrderProductService.delete(id))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("/{id}/edit")
    fun editExtraOrderProduct(
        @PathVariable id: Long,
        @RequestBody request: ExtraOrderProductEditSchema
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(extraOrderProductService.edit(id, request))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}