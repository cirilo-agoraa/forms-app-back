package agoraa.app.forms_back.products.transfer.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "product_register")
data class ProductRegisterModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val barcode: String,

    @Column(nullable = false)
    val store: String,

    @Column(nullable = false)
    val supplier: Long,

    @Column(nullable = false)
    val transferProduct: String,

    @Column(nullable = false)
    val reason: String,

    @Column(nullable = true)
    val description: String? = null,

    @Lob
    val productPhoto: ByteArray? = null,

    @Lob
    val barcodePhoto: ByteArray? = null,

    @Column(nullable = false)
    val cest: String,

    @Column(nullable = false)
    val ncm: String,

    @Column(nullable = false)
    val sector: String,

    @Column(name = "group_name")
    val group: String,

    @Column(nullable = false)
    val subgroup: String,

    @Column(nullable = false)
    val brand: String,

    @Column(nullable = true)
    val purchasePackage: String,

    @Column(nullable = true)
    val transferPackage: String,

    @Column(nullable = true)
    val grammage: String,

    @Column(nullable = true)
    val supplierReference: String,

    @Column(nullable = true)
    val productType: String,

    @Column(nullable = true)
    val name: String,

    @Column(nullable = false)
    val costPrice: String,      // <-- novo campo

    @Column(nullable = false)
    val salePrice: String,      

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)