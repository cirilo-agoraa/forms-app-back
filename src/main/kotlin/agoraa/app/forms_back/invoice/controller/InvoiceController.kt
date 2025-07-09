package agoraa.app.forms_back.invoice.controller

import agoraa.app.forms_back.invoice.dto.InvoiceDTO
import agoraa.app.forms_back.invoice.service.InvoiceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import agoraa.app.forms_back.invoice.model.Invoice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.FileInputStream

@RestController
@RequestMapping("/api/invoices")
class InvoiceController(
    private val service: InvoiceService
) {
    @GetMapping
    fun getAll(): List<InvoiceDTO> = service.findAll()

    @GetMapping("/retained")
    fun getRetainedInvoices(): ResponseEntity<List<Invoice>> {
        val retained = service.findAllRetained()
        return ResponseEntity.ok(retained)
    }
    @GetMapping("/retained/notify")
    fun notifyAllRetainedInvoices(): ResponseEntity<String> {
        service.notifyAllRetainedInvoices()
        return ResponseEntity.ok("Notificação enviada para todas as invoices retidas com status 0.")
    }
    
    @GetMapping("/bonificados")
    fun getBonificados(): ResponseEntity<List<Invoice>> {
        val bonificados = service.findAllBonificados()
        return ResponseEntity.ok(bonificados)
    }
    
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<InvoiceDTO> =
        service.findById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody dto: InvoiceDTO): InvoiceDTO = service.save(dto)


    @PatchMapping("/{id}/patch")
    fun patchInvoice(
        @PathVariable id: Long,
        @RequestBody request: Map<String, Any>
    ): ResponseEntity<Any> {
        val status = request["status"]?.toString()
        val retainedStatus = request["retainedStatus"]?.toString()?.toIntOrNull()
        println("Received status: $status")
        println("Received request: $request")
        return when {
            retainedStatus != null -> {
                val updated = service.patchRetainedStatus(id, retainedStatus)
                if (updated != null) ResponseEntity.ok(updated)
                else ResponseEntity.notFound().build()
            }
            status != null -> {
                val updated = service.patchBonitificationStatus(id, status)
                if (updated != null) ResponseEntity.ok(updated)
                else ResponseEntity.notFound().build()
            }
            else -> ResponseEntity.badRequest().body("Missing status or retainedStatus")
        }
    }

    @GetMapping("/bi/bases")
    fun importExcel(): ResponseEntity<List<Invoice>> {
        val invoices = mutableListOf<Invoice>()
        val excelPath = "F:/BI/Bases/entrada_de_nfs_coletadas.xlsx"
        FileInputStream(excelPath).use { inputStream ->
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)
            val headerRow = sheet.getRow(0)
            val header = (0 until headerRow.lastCellNum).map { idx ->
                headerRow.getCell(idx)?.stringCellValue?.trim() ?: ""
            }

            fun getCell(row: org.apache.poi.ss.usermodel.Row, name: String): String? {
                val idx = header.indexOf(name)
                return if (idx >= 0) row.getCell(idx)?.toString()?.trim() else null
            }

            fun getExcelDate(row: org.apache.poi.ss.usermodel.Row, name: String): LocalDateTime? {
                val idx = header.indexOf(name)
                if (idx < 0) return null
                val cell = row.getCell(idx) ?: return null
                return when (cell.cellType) {
                    org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                        val date = cell.dateCellValue
                        date?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDateTime()
                    }
                    org.apache.poi.ss.usermodel.CellType.STRING -> {
                        val dateStr = cell.stringCellValue.trim()
                        try {
                            LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        } catch (e: Exception) {
                            try {
                                LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                    else -> null
                }
            }

            for (rowIdx in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIdx) ?: continue
                invoices.add(
                    Invoice(
                        danfe = getCell(row, "Nota"),
                        wasSended = false,
                        loja = getCell(row, "Loja"),
                        nf = getCell(row, "Chave Acesso NF-e"),
                        liberado = false,
                        toliberation = false,
                        valorNota = getCell(row, "Valor Nota")?.toDoubleOrNull(),
                        wms = getCell(row, "WMS"),
                        toBonus = false,
                        bonusStatus = getCell(row, "bonusStatus"),
                        supplierName = getCell(row, "Fornecedor"),
                        dateEmissao = getExcelDate(row, "Data Emissão"),
                        dateEntrada = getExcelDate(row, "Data Entrada"),
                        createdAt = null
                    )
                )
            }
            workbook.close()
        }
        return ResponseEntity.ok(invoices)
    }

    @PostMapping("/retained")
    fun createRetainedInvoices(@RequestBody invoices: List<Invoice>): ResponseEntity<Any> {
        println("Received invoices for retention: ${invoices}")
        val missingDates = invoices.filter { it.retainedStatus == 1 && (it.dateEmissao == null || it.dateEntrada == null) }
        if (missingDates.isNotEmpty()) {
            return ResponseEntity.badRequest().body("Todas as invoices devem ter dateEmissao e dateEntrada preenchidos.")
        }
        val saved = service.saveAll(invoices.filter { it.retainedStatus == 1 })
        return ResponseEntity.ok(saved)
    }
}