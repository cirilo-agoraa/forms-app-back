package agoraa.app.forms_back.product_mix.controller

import agoraa.app.forms_back.product_mix.model.ProductMixModel
import agoraa.app.forms_back.product_mix.service.ProductMixService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import agoraa.app.forms_back.product_mix.dto.ProductMixRegisterRequest
import agoraa.app.forms_back.shared.service.ChatsacService

@RestController
@RequestMapping("/api/product-mix")
class ProductMixController(private val service: ProductMixService,
                          private val chatsacService: ChatsacService) {
    @PostMapping
    fun create(@RequestBody request: ProductMixRegisterRequest): ResponseEntity<ProductMixModel> {
            val saved = service.create(
                request.productCode,
                request.store,
                request.motive,
                request.foraDoMixStt,
                request.foraDoMixSmj,
                request.createdBy
            )



            return ResponseEntity.ok(saved)
    }

    @GetMapping
    fun getAll() = ResponseEntity.ok(service.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        service.getById(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}