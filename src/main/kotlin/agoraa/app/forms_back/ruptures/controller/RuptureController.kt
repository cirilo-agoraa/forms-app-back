package agoraa.app.forms_back.ruptures.controller

import agoraa.app.forms_back.ruptures.model.RupturaModel
import agoraa.app.forms_back.ruptures.service.RupturaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rupture")
class RupturaController(private val service: RupturaService) {
    @PostMapping
    fun create(@RequestBody ruptura: RupturaModel) = ResponseEntity.ok(service.create(ruptura))

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