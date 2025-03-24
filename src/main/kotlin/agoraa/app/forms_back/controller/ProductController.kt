package agoraa.app.forms_back.controller

import agoraa.app.forms_back.enums.MipsCategoriesEnum
import agoraa.app.forms_back.enums.SectorsEnum
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.schema.product.ProductSchema
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
        @RequestParam(defaultValue = "false") full: Boolean,
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(required = false) outOfMix: Boolean?,
        @RequestParam(required = false) supplierId: Long?,
        @RequestParam(required = false) supplierName: String?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) code: String?,
        @RequestParam(required = false) codes: List<String>?,
        @RequestParam(required = false) isResource: Boolean?,
        @RequestParam(required = false) stores: List<StoresEnum>?,
        @RequestParam(required = false) sector: SectorsEnum?,
        @RequestParam(required = false) mipCategories: List<MipsCategoriesEnum>?
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(
                productService.getAll(
                    full,
                    pagination,
                    page,
                    size,
                    sort,
                    direction,
                    outOfMix,
                    supplierId,
                    supplierName,
                    name,
                    code,
                    codes,
                    isResource,
                    stores,
                    sector,
                    mipCategories
                )
            )

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Long,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(productService.returnById(id))

    @PostMapping
    fun createProduct(
        @Valid @RequestBody request: ProductSchema,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request))
            }
        }
    }

    @PutMapping("/{id}/edit")
    fun editProduct(
        @PathVariable id: Long,
        @RequestBody request: ProductSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            productService.edit(id, request)
        )
    }

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateMultipleProducts(
        @Valid @RequestBody request: List<ProductSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                productService.editOrCreateMultiple(request)
                ResponseEntity.status(HttpStatus.OK).body("Products created or edited successfully")
            }
        }
    }
}