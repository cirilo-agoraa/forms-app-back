package agoraa.app.forms_back.controller

import agoraa.app.forms_back.enum.supplier.SupplierStatusEnum
import agoraa.app.forms_back.schema.supplier.SupplierCreateSchema
import agoraa.app.forms_back.service.SupplierService
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
        @RequestParam(defaultValue = "true") pagination: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        @RequestParam name: String?,
        @RequestParam status: List<SupplierStatusEnum>?,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK)
            .body(supplierService.getAll(pagination, page, size, sort, direction, name, status))

    @GetMapping("/{id}")
    fun getSupplierById(
        @PathVariable id: Long,
    ): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.OK).body(supplierService.getById(id))

    @PostMapping("/create-multiple")
    fun createSuppliers(
        @RequestBody @Valid request: List<SupplierCreateSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createMultiple(request))
        }
    }

    @PutMapping("/edit-or-create-multiple")
    fun editOrCreateSuppliers(
        @RequestBody @Valid request: List<SupplierCreateSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        return when {
            bindingResult.hasErrors() -> {
                val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
            }

            else -> ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.editOrCreateMultipleByName(request))
        }
    }
}
