package agoraa.app.forms_back.invoice.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "invoices")
data class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val danfe: String? = null,
    val wasSended: Boolean? = null,
    val loja: String? = null,
    val nf: String? = null,
    val liberado: Boolean? = null,
    val toliberation: Boolean? = null,
    val supplierId: Long? = null,
    val valorNota: Double? = null,
    val wms: String? = null,
    val toBonus: Boolean? = null,
    var bonusStatus: String? = null,
    val supplierName: String? = null,
    val dateEmissao: LocalDateTime? = null,
    val dateEntrada: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null
)