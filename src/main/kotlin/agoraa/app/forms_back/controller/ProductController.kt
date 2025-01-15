package agoraa.app.forms_back.controller

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.product.ProductDtoOptionsEnum
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
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam(defaultValue = "MINIMAL") dtoOptions: ProductDtoOptionsEnum,
        outOfMix: Boolean?,
        supplierId: Long?,
        supplierName: String?,
        name: String?,
        code: String?,
        isResource: Boolean?
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(
                productService.getAll(
                    pagination,
                    page,
                    size,
                    sort,
                    direction,
                    dtoOptions,
                    outOfMix,
                    supplierId,
                    supplierName,
                    name,
                    code,
                    isResource
                )
            )

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "MINIMAL") dtoOptions: ProductDtoOptionsEnum
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(productService.returnById(dtoOptions, id))

    // ADMIN ONLY

    @PostMapping("/create-multiple")
    fun createProducts(
        @RequestBody @Valid request: List<ProductCreateSchema>,
        @RequestParam(defaultValue = "MINIMAL") dtoOptions: ProductDtoOptionsEnum,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.CREATED).body(productService.createMultiple(dtoOptions, request))
        }
    }

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateProducts(
        @RequestBody @Valid request: List<ProductCreateSchema>,
        @RequestParam(defaultValue = "MINIMAL") dtoOptions: ProductDtoOptionsEnum,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.OK)
                .body(productService.editOrCreateMultipleByCodeAndStore(dtoOptions, request))
        }
    }
}