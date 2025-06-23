package agoraa.app.forms_back.invoice.service

import agoraa.app.forms_back.invoice.dto.InvoiceDTO
import agoraa.app.forms_back.invoice.model.Invoice
import agoraa.app.forms_back.invoice.repository.InvoiceRepository
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional
import agoraa.app.forms_back.shared.service.ChatsacService

@Service
class InvoiceService(
    private val repository: InvoiceRepository,
    private val whatsappService: ChatsacService
) {
    fun findAll(): List<InvoiceDTO> =
        repository.findAll().map { it.toDTO() }

        
    fun findAllRetained(): List<Invoice> = repository.findByRetainedStatusGreaterThan(0)
    fun findAllBonificados(): List<Invoice> = repository.findByToBonusTrue()

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

    fun saveAll(invoices: List<Invoice>): List<Invoice> {
        val savedInvoices = mutableListOf<Invoice>()
        val msg = StringBuilder()
        msg.appendLine("NFs com canhotos retidos:")
        
        for (invoice in invoices) {
            val saved = repository.save(invoice)
            savedInvoices.add(saved)
            msg.appendLine("  • ${invoice.danfe}/ ${invoice.supplierName} - Motivo: ${invoice.retainedMotive}")
        }

        // val number = "27999000862"
        val number = "663a53e93b0a671bbcb23c93"

        println(msg.toString())
        whatsappService.sendMsg(msg.toString(), number).subscribe()

        return savedInvoices
    }

    @Transactional
    fun patchRetainedStatus(id: Long, retainedStatus: Int): InvoiceDTO? {
        val invoiceOpt = repository.findById(id)
        if (invoiceOpt.isEmpty) return null
        val invoice = invoiceOpt.get()
        invoice.retainedStatus = retainedStatus
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
    // val msg = buildString {
    //     appendLine("NFs com canhotos retidos:")
    //     if (entity.retainedStatus > 0) {
    //         appendLine("  • ${entity.nf}/ {entity.supplierName} - Motivo: (${entity.retainedStatus})")
    //     }
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

