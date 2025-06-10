package agoraa.app.forms_back.products.transfer.dto

data class ProductRegisterRequest(
    val name: String,
    val barcode: String,
    val store: String,
    val supplier: Long,
    val transferProduct: String,
    val reason: String,
    val productPhoto: ByteArray?,
    val barcodePhoto: ByteArray?,
    val cest: String,
    val ncm: String,
    val sector: String,
    val group: String,
    val subgroup: String,
    val brand: String,
    val purchasePackage: String,
    val transferPackage: String,
    val grammage: String,
    val supplierReference: String,
    val productType: String,
    val description: String? = null 
)