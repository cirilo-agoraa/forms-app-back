package agoraa.app.forms_back.resource_mips.resource_mip_products.dto.request

import agoraa.app.forms_back.products.products.model.ProductModel
import jakarta.validation.constraints.NotNull

data class ResourceMipProductsRequest(
    @field:NotNull(message = "quantity is required")
    val quantity: Int,

    @field:NotNull(message = "product is required")
    val product: ProductModel
)