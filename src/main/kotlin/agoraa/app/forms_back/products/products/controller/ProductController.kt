package agoraa.app.forms_back.products.products.controller

import agoraa.app.forms_back.products.products.dto.request.ProductRequest
import agoraa.app.forms_back.products.products.dto.request.ProductsRequestAnticipation
import agoraa.app.forms_back.products.products.service.ProductService
import agoraa.app.forms_back.shared.enums.MipsCategoriesEnum
import agoraa.app.forms_back.shared.enums.ProductGroupsEnum
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
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
        @RequestParam(required = false) sector: ProductSectorsEnum?,
        @RequestParam(required = false) mipCategories: List<MipsCategoriesEnum>?,
        @RequestParam(required = false) currentStockGreaterThan: Double? = null,
        @RequestParam(required = false) groupNamesNotIn: List<ProductGroupsEnum>? = null,
        @RequestParam(required = false) sectorsNotIn: List<ProductSectorsEnum>? = null,
        @RequestParam(required = false) salesLastSevenDaysEqual: Double? = null
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
                    mipCategories,
                    currentStockGreaterThan,
                    groupNamesNotIn,
                    sectorsNotIn,
                    salesLastSevenDaysEqual
                )
            )

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Long,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(productService.returnById(id))

    @GetMapping("/quantity")
    fun getProductsQuantity(
        @RequestParam(required = false) sectorsIn: List<ProductSectorsEnum>? = null,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(productService.getQuantity(sectorsIn))

    @PostMapping
    fun createProduct(
        @Valid @RequestBody request: ProductsRequestAnticipation,
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
        @RequestBody request: ProductsRequestAnticipation
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            productService.edit(id, request)
        )
    }

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateMultipleProducts(
        @Valid @RequestBody request: List<ProductRequest>,
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

    @GetMapping("/code/{code}")
    fun getProductByCode(
        @PathVariable code: String,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(productService.getProductByCode(code))
}