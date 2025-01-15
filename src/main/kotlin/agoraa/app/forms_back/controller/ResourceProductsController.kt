package agoraa.app.forms_back.controller

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.service.ResourceProductsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/resource-products")
class ResourceProductsController(private val resourceProductsService: ResourceProductsService) {

    @GetMapping("/by-resource/{resourceId}")
    fun getByResourceId(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable resourceId: Long,
        name: String?,
        code: String?,
        sector: String?
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(resourceProductsService.findByResourceId(customUserDetails, resourceId, name, code, sector))
    }
}