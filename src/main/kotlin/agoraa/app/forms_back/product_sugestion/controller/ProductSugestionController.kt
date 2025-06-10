package agoraa.app.forms_back.product_sugestion.controller

import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionRequest
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import agoraa.app.forms_back.product_sugestion.service.ProductSugestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/product-sugestion")
class ProductSugestionController(
    private val service: ProductSugestionService
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<ProductSugestionModel>> {
        val suggestions = service.getAll()
        return ResponseEntity.ok(suggestions)
    }

    @PostMapping(consumes = ["multipart/form-data"])
    fun create(
        @RequestParam name: String,
        @RequestParam(required = false) description: String?,
        @RequestPart(required = false) productImage: MultipartFile?
    ): ResponseEntity<ProductSugestionModel> {
        val data = ProductSugestionRequest(name = name, description = description)
        val saved = service.create(data, productImage)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ProductSugestionModel> {
        val suggestion = service.getById(id)
        return if (suggestion != null)
            ResponseEntity.ok(suggestion)
        else
            ResponseEntity.notFound().build()
    }

    @PatchMapping("/{id}/patch", consumes = ["multipart/form-data"])
    fun update(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam status: Int = 0,
        @RequestParam(required = false) description: String?,
        @RequestPart(required = false) productImage: MultipartFile?
    ): ResponseEntity<ProductSugestionModel> {
        val data = ProductSugestionRequest(name = name, description = description, status = status)
        val updated = service.update(id, data, productImage)
        return if (updated != null)
            ResponseEntity.ok(updated)
        else
            ResponseEntity.notFound().build()
    }
}