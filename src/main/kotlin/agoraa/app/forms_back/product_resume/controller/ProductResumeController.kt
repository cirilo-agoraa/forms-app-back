package agoraa.app.forms_back.product_resume.controller

import agoraa.app.forms_back.product_resume.service.ProductResumeService
import agoraa.app.forms_back.products_resume.dto.ProductsResumeDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products-resume")
class ProductResumeController(
    private val productResumeService: ProductResumeService
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<ProductsResumeDto>> =
        ResponseEntity.ok(productResumeService.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ProductsResumeDto?> =
        ResponseEntity.ok(productResumeService.getById(id))

    @GetMapping("/code/{code}")
    fun getByCode(@PathVariable code: String): ResponseEntity<ProductsResumeDto?> {
        val dto = productResumeService.getByCode(code)
        return if (dto != null) ResponseEntity.ok(dto) else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun create(@RequestBody dto: ProductsResumeDto): ResponseEntity<ProductsResumeDto> =
        ResponseEntity.ok(productResumeService.save(dto))

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @RequestBody dto: ProductsResumeDto
    ): ResponseEntity<ProductsResumeDto> {
        val patched = productResumeService.patch(id, dto)
        return if (patched != null) {
            ResponseEntity.ok(patched)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}