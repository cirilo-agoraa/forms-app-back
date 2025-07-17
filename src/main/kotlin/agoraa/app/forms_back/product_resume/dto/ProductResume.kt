package agoraa.app.forms_back.products_resume.dto

data class ProductsResumeDto(
    val id: Long,
    val code: String,
    val name: String,
    val barcode: String,
    val store: String,
    val outOfMixSmj: Boolean,
    val outOfMixStt: Boolean,
    val sector: String,
    val groupName: String?,
    val subgroup: String?,
    val brand: String?,
    val supplierName: String?,
    val supplierId: Long?
)