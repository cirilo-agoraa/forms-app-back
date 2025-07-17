package agoraa.app.forms_back.products_resume.model

import jakarta.persistence.*

@Entity
@Table(name = "products_resume")
data class ProductsResumeModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val code: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val barcode: String,

    @Column(nullable = false)
    val store: String,

    @Column(name = "out_of_mix_smj", nullable = false)
    val outOfMixSmj: Boolean,

    @Column(name = "out_of_mix_stt", nullable = false)
    val outOfMixStt: Boolean,

    @Column(nullable = false)
    val sector: String,

    @Column(name = "group_name")
    val groupName: String? = null,

    @Column(name = "subgroup")
    val subgroup: String? = null,

    @Column(name = "brand")
    val brand: String? = null,

    @Column(name = "supplier_name")
    val supplierName: String? = null,

    @Column(name = "supplier_id")
    val supplierId: Long? = null
)