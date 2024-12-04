package agoraa.app.forms_back.controller

import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.schema.supplier.SupplierCreateSchema
import agoraa.app.forms_back.schema.supplier.SupplierEditMultipleSchema
import agoraa.app.forms_back.schema.supplier.SupplierEditSchema
import agoraa.app.forms_back.service.SupplierService
import jakarta.validation.Valid
import org.springdoc.api.OpenApiResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/suppliers")
class SupplierController(private val supplierService: SupplierService) {

    // ADMIN ENDPOINTS

    @GetMapping("/{id}")
    fun getSupplierById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(supplierService.findById(id))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PostMapping("/create-multiple")
    fun createSuppliers(
        @RequestBody @Valid request: List<SupplierCreateSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Iterable<Any>> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.badRequest().body(errors)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createBatch(request))
    }

    @PostMapping
    fun createSupplier(
        @RequestBody @Valid request: SupplierCreateSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(request))
    }

    @PutMapping("/{id}/edit")
    fun editSupplier(
        @PathVariable id: Long,
        @RequestBody @Valid request: SupplierEditSchema,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return try {
            ResponseEntity.status(HttpStatus.OK).body(supplierService.edit(id, request))
        } catch (e: ResourceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("/edit-multiple")
    fun editSuppliers(
        @RequestBody @Valid request: List<SupplierEditMultipleSchema>,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        return ResponseEntity.status(HttpStatus.OK).body(supplierService.editMultipleByName(request))
    }

    // USER ENDPOINTS

    @GetMapping
    fun getAllSuppliers(
        @RequestParam(defaultValue = "true") pagination: String,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "") status: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(supplierService.findAll(pagination, name, status, page, size, sort, direction))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }
}