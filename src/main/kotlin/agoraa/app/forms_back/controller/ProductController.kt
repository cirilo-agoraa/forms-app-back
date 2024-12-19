package agoraa.app.forms_back.controller

import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.schema.product.ProductCreateSchema
import agoraa.app.forms_back.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getAllProducts(
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "true") convertToDTO: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam outOfMix: Boolean?,
        @RequestParam supplierId: Long?,
        @RequestParam supplierName: String?,
        @RequestParam name: String?,
        @RequestParam code: String?,
        @RequestParam store: List<StoresEnum>?
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(
                productService.findAll(
                    pagination,
                    convertToDTO,
                    outOfMix,
                    supplierId,
                    supplierName,
                    name,
                    code,
                    store,
                    page,
                    size,
                    sort,
                    direction
                )
            )

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(productService.returnById(id))

    // ADMIN ONLY

    @PostMapping("/create-multiple")
    fun createProducts(
        @RequestBody @Valid request: List<ProductCreateSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createMultiple(request))
    }

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateProducts(
        @RequestBody @Valid request: List<ProductCreateSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }

        return ResponseEntity.status(HttpStatus.OK).body(productService.editOrCreateMultipleByCodeAndStore(request))
    }
}