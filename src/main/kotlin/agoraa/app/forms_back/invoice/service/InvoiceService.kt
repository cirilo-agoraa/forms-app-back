package agoraa.app.forms_back.invoice.service

import agoraa.app.forms_back.invoice.dto.InvoiceDTO
import agoraa.app.forms_back.invoice.model.Invoice
import agoraa.app.forms_back.invoice.repository.InvoiceRepository
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
class InvoiceService(
    private val repository: InvoiceRepository
) {
    fun findAll(): List<InvoiceDTO> =
        repository.findAll().map { it.toDTO() }

    fun save(dto: InvoiceDTO): InvoiceDTO {
        val entity = dto.toEntity()
        return repository.save(entity).toDTO()
    }

    fun findById(id: Long): InvoiceDTO? =
        repository.findById(id).orElse(null)?.toDTO()

    @Transactional
    fun patchBonitificationStatus(id: Long, status: String): InvoiceDTO? {
        val invoiceOpt = repository.findById(id)
        if (invoiceOpt.isEmpty) return null
        val invoice = invoiceOpt.get()
        invoice.bonusStatus = status        
        return repository.save(invoice).toDTO()
    }
}

// Extension functions for mapping
fun Invoice.toDTO() = InvoiceDTO(
    id = id,
    danfe = danfe,
    wasSended = wasSended,
    loja = loja,
    nf = nf,
    liberado = liberado,
    toliberation = toliberation,
    supplierId = supplierId,
    valorNota = valorNota,
    wms = wms,
    toBonus = toBonus,
    bonusStatus = bonusStatus,
    supplierName = supplierName,
    dateEmissao = dateEmissao,
    dateEntrada = dateEntrada,
    createdAt = createdAt
)

fun InvoiceDTO.toEntity() = Invoice(
    id = id ?: 0,
    danfe = danfe,
    wasSended = wasSended,
    loja = loja,
    nf = nf,
    liberado = liberado,
    toliberation = toliberation,
    supplierId = supplierId,
    valorNota = valorNota,
    wms = wms,
    toBonus = toBonus,
    bonusStatus = bonusStatus,
    supplierName = supplierName,
    dateEmissao = dateEmissao,
    dateEntrada = dateEntrada,
    createdAt = createdAt
)

