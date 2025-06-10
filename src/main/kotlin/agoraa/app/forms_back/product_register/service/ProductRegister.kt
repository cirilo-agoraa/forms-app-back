package agoraa.app.forms_back.products.transfer.service

import agoraa.app.forms_back.products.transfer.dto.ProductRegisterRequest
import agoraa.app.forms_back.products.transfer.model.ProductRegisterModel
import agoraa.app.forms_back.products.transfer.repository.ProductRegisterRepository
import agoraa.app.forms_back.shared.service.ChatsacService
import org.springframework.stereotype.Service
import agoraa.app.forms_back.suppliers.suppliers.repository.SupplierRepository // Corrija o import conforme o seu projeto

@Service
class ProductRegisterService(
    private val repository: ProductRegisterRepository,
    private val chatsacService: ChatsacService,
    private val supplierRepository: SupplierRepository,
) {
    fun create(request: ProductRegisterRequest): ProductRegisterModel {
        val entity = ProductRegisterModel(
            name = request.name,
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
            productType = request.productType,
            description = request.description // <-- vírgula corrigida e campo adicionado
        )
        val saved = repository.save(entity)
        val nome = "${request.name} ${request.brand} ${request.grammage}".trim()

        val fornecedor = supplierRepository.findById(request.supplier)
            .map { it.name }
            .orElse("Fornecedor não encontrado")

        println("Fornecedor: $fornecedor")
        val msg = buildString {
            appendLine("Solicitação de cadastro de produto:")
            if (nome.isNotBlank()) appendLine("• Nome Completo: $nome")
            request.barcode?.takeIf { it.isNotBlank() }?.let { appendLine("• Código de barras: $it") }
            request.store?.takeIf { it.isNotBlank() }?.let { appendLine("• Loja: $it") }
            fornecedor.name.takeIf { it.isNotBlank() }?.let { appendLine("• Fornecedor: $it") }
            request.transferProduct?.takeIf { it.isNotBlank() }?.let { appendLine("• Produto para transferência: $it") }
            request.reason?.takeIf { it.isNotBlank() }?.let { appendLine("• Motivo: $it") }
            request.cest?.takeIf { it.isNotBlank() }?.let { appendLine("• CEST: $it") }
            request.ncm?.takeIf { it.isNotBlank() }?.let { appendLine("• NCM: $it") }
            request.sector?.takeIf { it.isNotBlank() }?.let { appendLine("• Setor: $it") }
            request.group?.takeIf { it.isNotBlank() }?.let { appendLine("• Grupo: $it") }
            request.subgroup?.takeIf { it.isNotBlank() }?.let { appendLine("• Subgrupo: $it") }
            request.brand?.takeIf { it.isNotBlank() }?.let { appendLine("• Marca: $it") }
            request.purchasePackage?.takeIf { it.isNotBlank() }?.let { appendLine("• Embalagem de compra: $it") }
            request.transferPackage?.takeIf { it.isNotBlank() }?.let { appendLine("• Embalagem de transferência: $it") }
            request.grammage?.takeIf { it.isNotBlank() }?.let { appendLine("• Gramatura: $it") }
            request.supplierReference?.takeIf { it.isNotBlank() }?.let { appendLine("• Referência do fornecedor: $it") }
            request.productType?.takeIf { it.isNotBlank() }?.let { appendLine("• Tipo de produto: $it") }
            request.description?.takeIf { it.isNotBlank() }
                ?.let { appendLine("• Descrição: $it") }
                ?: appendLine("• Descrição: Nenhuma descrição fornecida")
        }

        val number = "663a53e93b0a671bbcb23c93"
        chatsacService.sendMsg(msg, number).subscribe()

        request.productPhoto?.let { photo ->
            chatsacService.sendImg(photo, "Foto do Produto", number).subscribe()
        }
        request.barcodePhoto?.let { photo ->
            chatsacService.sendImg(photo, "Foto do Código de Barras", number).subscribe()
        }

        return saved
    }
}