package agoraa.app.forms_back.products.transfer.controller

import agoraa.app.forms_back.products.transfer.dto.ProductRegisterRequest
import agoraa.app.forms_back.products.transfer.service.ProductRegisterService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile // <-- Adicione esta linha

@RestController
@RequestMapping("/api/product-register")
class ProductRegisterController(
    private val service: ProductRegisterService
) {
@PostMapping(consumes = ["multipart/form-data"])
    fun create(
        @RequestParam barcode: String,
        @RequestParam store: String,
        @RequestParam supplier: Long,
        @RequestParam transferProduct: String,
        @RequestParam reason: String,
        @RequestParam cest: String,
        @RequestParam ncm: String,
        @RequestParam sector: String,
        @RequestParam group: String,
        @RequestParam subgroup: String,
        @RequestParam brand: String,
        @RequestParam purchasePackage: String,
        @RequestParam transferPackage: String,
        @RequestParam grammage: String,
        @RequestParam supplierReference: String,
        @RequestParam productType: String,
        @RequestParam name: String,
        @RequestParam(required = false) description: String?,
        @RequestPart(required = false) productPhoto: MultipartFile?,
        @RequestPart(required = false) barcodePhoto: MultipartFile?
    ): ResponseEntity<Any> {
        val dto = ProductRegisterRequest(
            barcode = barcode,
            store = store,
            supplier = supplier,
            transferProduct = transferProduct,
            reason = reason,
            productPhoto = productPhoto?.bytes,
            barcodePhoto = barcodePhoto?.bytes,
            cest = cest,
            ncm = ncm,
            sector = sector,
            group = group,
            subgroup = subgroup,
            brand = brand,
            purchasePackage = purchasePackage,
            transferPackage = transferPackage,
            grammage = grammage,
            supplierReference = supplierReference,
            productType = productType,
            name = name,
            description = description

        )
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))
    }
}