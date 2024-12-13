package agoraa.app.forms_back.controller

import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.schema.product.ProductCreateSchema
import agoraa.app.forms_back.schema.product.ProductEditSchema
import agoraa.app.forms_back.service.ProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    // ADMIN ENDPOINTS

    @PostMapping("/create-multiple")
    fun createProducts(
        @RequestBody @Valid request: List<ProductCreateSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Iterable<Any>> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createBatch(request))
    }

    @PostMapping
    fun createProducts(
        @RequestBody @Valid request: ProductCreateSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request))
    }

    @PutMapping("/{id}/edit")
    fun editProduct(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductEditSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }

        return try {
            ResponseEntity.status(HttpStatus.OK).body(productService.edit(id, request))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    // USER ENDPOINTS

    @GetMapping
    fun getAllProducts(
        @RequestParam(defaultValue = "") outOfMix: String,
        @RequestParam(defaultValue = "0") supplierId: Long,
        @RequestParam(defaultValue = "") supplierName: String,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "") code: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Page<ProductModel>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAll(outOfMix, supplierId, supplierName, name, code, page, size, sort, direction))

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(productService.findById(id))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}