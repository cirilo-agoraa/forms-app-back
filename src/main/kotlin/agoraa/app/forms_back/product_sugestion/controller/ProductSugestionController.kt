package agoraa.app.forms_back.product_sugestion.controller

import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionRequest
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import agoraa.app.forms_back.product_sugestion.service.ProductSugestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionLineRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

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
        @RequestParam isProductLine: Boolean = false,
        @RequestPart(required = false) productImage: MultipartFile?
    ): ResponseEntity<ProductSugestionModel> {
        val data = ProductSugestionRequest(name = name, description = description, isProductLine = isProductLine)
        val saved = service.create(data, productImage)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

@GetMapping("/{id}")
fun getById(@PathVariable id: Long): ResponseEntity<ProductSugestionRequest> {
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
        @RequestPart(required = false) productImage: MultipartFile?,
        @RequestParam(required = false) costPrice: Double? = null,
        @RequestParam(required = false) salePrice: Double? = null,
        @RequestParam(required = false) supplierId: Long? = null,
        @RequestParam(required = false) justification: String? = null,
        @RequestParam(required = false) isProductLine: Boolean = false,
        @RequestParam(required = false) sector: String? = null,
        @RequestParam(required = false) products: String? = null // JSON dos produtos
    ): ResponseEntity<ProductSugestionModel> {
        val data = ProductSugestionRequest(
            name = name,
            description = description,
            status = status,
            costPrice = costPrice,
            salePrice = salePrice,
            supplierId = supplierId,
            justification = justification,
            sector = sector,
            isProductLine = isProductLine
        )

        // Permite products ser array ou objeto único
        val productLines = if (isProductLine && !products.isNullOrBlank()) {
            val mapper = jacksonObjectMapper()
            try {
                mapper.readValue(products, Array<ProductSugestionLineRequest>::class.java).toList()
            } catch (ex: Exception) {
                listOf(mapper.readValue(products, ProductSugestionLineRequest::class.java))
            }
        } else {
            emptyList()
        }

        val updated = service.update(id, data, productImage, productLines)
        return if (updated != null)
            ResponseEntity.ok(updated)
        else
            ResponseEntity.notFound().build()
    }
}