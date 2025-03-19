package agoraa.app.forms_back.controller

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import agoraa.app.forms_back.schema.supplier.SupplierSchema
import agoraa.app.forms_back.service.suppliers.SupplierService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/suppliers")
class SupplierController(private val supplierService: SupplierService) {

    @GetMapping
    fun getAllSuppliers(
        @RequestParam(required = false, defaultValue = "false") full: Boolean,
        @RequestParam(required = false, defaultValue = "true") pagination: Boolean,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "id") sort: String,
        @RequestParam(required = false, defaultValue = "asc") direction: String,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) exchange: Boolean?,
        @RequestParam(required = false) status: List<SupplierStatusEnum>?,
        @RequestParam(required = false) names: List<String>?,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(supplierService.getAll(full, pagination, page, size, sort, direction, name, exchange, status, names))

    @GetMapping("/{id}")
    fun getSupplierById(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") full: Boolean
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(supplierService.getById(id, full))

    @PostMapping
    fun createSupplier(
        @Valid @RequestBody request: SupplierSchema,
        bindingResult: BindingResult,
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(request))
            }
        }
    }

    @PutMapping("/{id}/edit")
    fun editSupplier(
        @PathVariable id: Long,
        @RequestBody request: SupplierSchema
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(
            supplierService.edit(id, request)
        )
    }

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateMultipleSuppliers(
        @Valid @RequestBody request: List<SupplierSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> {
                supplierService.editOrCreateMultiple(request)
                ResponseEntity.status(HttpStatus.OK).body("Products created or edited successfully")
            }
        }
    }
}
