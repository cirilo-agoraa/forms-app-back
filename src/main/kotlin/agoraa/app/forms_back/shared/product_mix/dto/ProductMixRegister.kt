package agoraa.app.forms_back.product_mix.dto

import agoraa.app.forms_back.users.users.dto.response.UserResponse

import java.time.LocalDateTime
import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.products_resume.dto.ProductsResumeDto
import agoraa.app.forms_back.users.users.model.UserModel

data class ProductMixRegisterRequest(
    val productCode: String,
    val foraDoMixStt: Boolean = false,
    val foraDoMixSmj: Boolean = false,
    val store: String? = "AMBAS",
    val motive: String? = "",
    val hasProcessed: Boolean? = null,
    val createdBy: Long? = null
)
data class ProductMixWithProductResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val productCode: String?,
    val foraDoMixStt: Boolean = false,
    val foraDoMixSmj: Boolean = false,
    val store: String? = "AMBAS",
    val motive: String? = "",
    val hasProcessed: Boolean? = null,
    val product: ProductsResumeDto? = null,
    val createdBy: Long? = null,
    val user: UserResponse? = null // <-- troque para UserResponse?
)