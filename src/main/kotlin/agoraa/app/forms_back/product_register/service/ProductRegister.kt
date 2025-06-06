package agoraa.app.forms_back.products.transfer.service

import agoraa.app.forms_back.products.transfer.dto.ProductRegisterRequest
import agoraa.app.forms_back.products.transfer.model.ProductRegisterModel
import agoraa.app.forms_back.products.transfer.repository.ProductRegisterRepository
import agoraa.app.forms_back.shared.service.ChatsacService
import org.springframework.stereotype.Service

@Service
class ProductRegisterService(
    private val repository: ProductRegisterRepository,
    private val chatsacService: ChatsacService
) {
    fun create(request: ProductRegisterRequest): ProductRegisterModel {
        val entity = ProductRegisterModel(
            barcode = request.barcode,
            store = request.store,
            supplier = request.supplier,
            transferProduct = request.transferProduct,
            reason = request.reason,
            productPhoto = request.productPhoto,
            barcodePhoto = request.barcodePhoto,
            cest = request.cest,
            ncm = request.ncm,
            sector = request.sector,
            group = request.group,
            subgroup = request.subgroup,
            brand = request.brand,
            purchasePackage = request.purchasePackage,
            transferPackage = request.transferPackage,
            grammage = request.grammage,
            supplierReference = request.supplierReference,
            productType = request.productType
        )
        val saved = repository.save(entity)

        val msg = """
            Solicitação de cadastro de produto:
            • Código de barras: ${request.barcode}
            • Loja: ${request.store}
            • Fornecedor: ${request.supplier}
            • Produto para transferência: ${request.transferProduct}
            • Motivo: ${request.reason}
            • CEST: ${request.cest}
            • NCM: ${request.ncm}
            • Setor: ${request.sector}
            • Grupo: ${request.group}
            • Subgrupo: ${request.subgroup}
            • Marca: ${request.brand}
            • Embalagem de compra: ${request.purchasePackage}
            • Embalagem de transferência: ${request.transferPackage}
            • Gramatura: ${request.grammage}
            • Referência do fornecedor: ${request.supplierReference}
            • Tipo de produto: ${request.productType}
        """.trimIndent()

        val number = "663a53e93b0a671bbcb23c93"
        chatsacService.sendMsg(msg, number).subscribe()

        request.productPhoto?.let {
            chatsacService.sendImg(it, "Foto do Produto", number).subscribe()
        }
        request.barcodePhoto?.let {
            chatsacService.sendImg(it, "Foto do Código de Barras", number).subscribe()
        }

        return saved
    }
}