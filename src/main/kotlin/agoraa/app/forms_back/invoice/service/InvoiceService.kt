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

        // Agrupa as invoices por loja
        val invoicesPorLoja = invoices.groupBy { it.loja }

        // Para cada loja, monta e envia a mensagem
        invoicesPorLoja.forEach { (loja, invoicesDaLoja) ->
            val lojaNome = when (loja) {
                "1" -> "SMJ"
                "2" -> "STT"
                else -> "Desconhecida"
            }
            val msg = StringBuilder()
            msg.appendLine("NFs com canhotos retidos - Loja $lojaNome:")
            for (invoice in invoicesDaLoja) {
                val saved = repository.save(invoice)
                savedInvoices.add(saved)
                msg.appendLine("  • ${invoice.danfe}/ ${invoice.supplierName} - Motivo: ${invoice.retainedMotive}")
            }
            val phoneNumber = when (loja) {
                "1" -> "663a53e93b0a671bbcb23c93"
                "2" -> "663a53e93b0a671bbcb23c93"
                else -> "663a53e93b0a671bbcb23c93"
            }
            println(msg.toString())
            whatsappService.sendMsg(msg.toString(), phoneNumber).subscribe()
        }

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

