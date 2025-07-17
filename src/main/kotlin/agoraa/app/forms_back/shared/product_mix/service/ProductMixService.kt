package agoraa.app.forms_back.product_mix.service

import agoraa.app.forms_back.product_mix.model.ProductMixModel
import agoraa.app.forms_back.product_mix.repository.ProductMixRepository
import org.springframework.stereotype.Service
import agoraa.app.forms_back.product_mix.dto.ProductMixWithProductResponse
import agoraa.app.forms_back.products.products.repository.ProductRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import agoraa.app.forms_back.products.products.service.ProductService
import java.time.format.DateTimeFormatter
import agoraa.app.forms_back.product_resume.service.ProductResumeService
import agoraa.app.forms_back.shared.service.ChatsacService

@Service
class ProductMixService(
    private val repository: ProductMixRepository,
    private val productRepository: ProductRepository,
    private val productService: ProductService,
    private val productResumeService: ProductResumeService,
    private val chatsacService: ChatsacService,
) {
    fun create(
        productCode: String,
        store: String? = "AMBAS",
        motive: String? = "",
        foraDoMixStt: Boolean = false,
        foraDoMixSmj: Boolean = false
    ): ProductMixModel {
        val productMix = ProductMixModel(
            productCode = productCode,
            store = store,
            motive = motive,
            foraDoMixStt = foraDoMixStt,
            foraDoMixSmj = foraDoMixSmj

        )
        val saved = repository.save(productMix)
        return saved
    }

    fun getAll(): List<ProductMixWithProductResponse> =
        repository.findAll()
            .sortedByDescending { it.createdAt }
            .map { mix ->
                ProductMixWithProductResponse(
                    id = mix.id,
                    createdAt = mix.createdAt,
                    productCode = mix.productCode,
                    product = productResumeService.getByCode(mix.productCode),
                    hasProcessed = mix.hasProcessed ?: false,
                    foraDoMixStt = mix.foraDoMixStt,
                    foraDoMixSmj = mix.foraDoMixSmj,
                    store = mix.store ?: "AMBAS",
                    motive = mix.motive ?: ""
                )
            }

    fun getById(id: Long): ProductMixModel? = repository.findById(id).orElse(null)
    fun delete(id: Long) = repository.deleteById(id)

    //     private fun logToExcel(productMix: ProductMixModel) {
            
    //     val excelPath = "F:/BI/Bases/log_mudancas_relatorio_tresman.xlsx"
    //     val file = File(excelPath)
    //     val workbook = if (file.exists()) {
    //         FileInputStream(file).use { fis -> WorkbookFactory.create(fis) as XSSFWorkbook }
    //     } else {
    //         XSSFWorkbook()
    //     }
    //     val product = productResumeService.getByCode(productMix.productCode)

    //     val sheet = workbook.getSheet("Solicitações do Portal") ?: workbook.createSheet("Solicitações do Portal")

    //     if (sheet.lastRowNum == 0 && sheet.getRow(0) == null) {
    //         val header = sheet.createRow(0)
    //         header.createCell(0).setCellValue("Data")
    //         header.createCell(1).setCellValue("ProductCode")
    //         header.createCell(2).setCellValue("ProductName")
    //         header.createCell(3).setCellValue("ForaDoMix")
    //         header.createCell(4).setCellValue("Store")
    //         header.createCell(5).setCellValue("Motive")
    //     }
    //     val rowNum = sheet.lastRowNum + 1
    //     val row = sheet.createRow(rowNum)
    //     val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    //     row.createCell(0).setCellValue(productMix.createdAt.format(formatter))
    //     row.createCell(1).setCellValue(product.code)
    //     row.createCell(2).setCellValue(product.name)
    //     row.createCell(3).setCellValue(if (productMix.foraDoMix) "Incluir Mix" else "Retirar Mix")
    //     row.createCell(4).setCellValue(productMix.store ?: "AMBAS")
    //     row.createCell(5).setCellValue(productMix.motive ?: "")
    //     FileOutputStream(file).use { fos -> workbook.write(fos) }
    //     workbook.close()
    // }

}