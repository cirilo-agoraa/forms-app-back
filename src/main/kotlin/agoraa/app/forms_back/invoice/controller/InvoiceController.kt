package agoraa.app.forms_back.invoice.controller

import agoraa.app.forms_back.invoice.dto.InvoiceDTO
import agoraa.app.forms_back.invoice.service.InvoiceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/invoices")
class InvoiceController(
    private val service: InvoiceService
) {
    @GetMapping
    fun getAll(): List<InvoiceDTO> = service.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<InvoiceDTO> =
        service.findById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody dto: InvoiceDTO): InvoiceDTO = service.save(dto)


    @PatchMapping("/{id}/patch")
    fun patchInvoice(
        @PathVariable id: Long,
        @RequestBody request: Map<String, Any>
    ): ResponseEntity<Any> {
        val status = request["status"]?.toString()
        println("Received status: $status")
        println("Received request: $request")
        if (status == null) return ResponseEntity.badRequest().body("Missing status")
        val updated = service.patchBonitificationStatus(id, status)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }
}