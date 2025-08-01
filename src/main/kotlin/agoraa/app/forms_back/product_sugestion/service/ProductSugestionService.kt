package agoraa.app.forms_back.product_sugestion.service

import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionRequest
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import agoraa.app.forms_back.product_sugestion.repository.ProductSugestionRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import agoraa.app.forms_back.shared.service.ChatsacService
import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionLineRequest
import agoraa.app.forms_back.product_sugestion.service.ProductSugestionLineService
import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionLineResponse
import org.springframework.transaction.annotation.Transactional
import java.util.Base64
import agoraa.app.forms_back.users.users.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import agoraa.app.forms_back.suppliers.suppliers.service.SupplierService
import java.text.NumberFormat
import java.util.Locale

@Service
class ProductSugestionService(
    private val repository: ProductSugestionRepository,
    private val chatsacService: ChatsacService,
    private val productSugestionLineService: ProductSugestionLineService, // adicione esta linha
    private val supplierService: SupplierService,
    @Autowired private val userService: UserService

) {
    fun create(request: ProductSugestionRequest, productImage: MultipartFile?): ProductSugestionModel {
        val authUser = userService.getAuthUser()
        val entity = ProductSugestionModel(
            name = request.name,
            description = request.description,
            isProductLine = request.isProductLine,
            productImage = productImage?.bytes,
            createdBy = authUser?.id, 
        )
        val resp =  repository.save(entity)

        val msg = """
            Nova Sugestão de Produto
            Loja Solicitante: ${authUser?.store ?: "N/A"}
            Produto: ${request.name}
            Linha Completa: ${if (request.isProductLine) "Sim" else "Não"}
            Descrição: ${request.description ?: "Nenhuma descrição fornecida"}
        """.trimIndent()

        chatsacService.sendMsg(
            number = "663a53e93b0a671bbcb23c93",
            message = msg,
        ).subscribe()

        if (productImage != null) {
            chatsacService.sendImg(
                imageBytes = productImage.bytes,
                fileName = "produto_sugerido.jpg",
                number = "663a53e93b0a671bbcb23c93",
                caption = "Imagem do produto sugerido: ${request.name}"
            ).subscribe()
        }

        return resp
    }

    // Exemplo de conversão no service
    fun getById(id: Long): ProductSugestionRequest? {
        val suggestion = repository.findById(id).orElse(null) ?: return null
        val lines = productSugestionLineService.findByProductSugestion(suggestion)
        val productImageBase64 = suggestion.productImage?.let { Base64.getEncoder().encodeToString(it) }

        return ProductSugestionRequest(
            name = suggestion.name,
            description = suggestion.description,
            status = suggestion.status,
            productImage = productImageBase64, // agora retorna a imagem como base64 string
            costPrice = suggestion.costPrice,
            salePrice = suggestion.salePrice,
            supplierId = suggestion.supplierId,
            justification = suggestion.justification,
            sector = suggestion.sector,
            isProductLine = suggestion.isProductLine,
            lines = lines.map {
                ProductSugestionLineResponse(
                    id = it.id,
                    name = it.name,
                    costPrice = it.costPrice,
                    salePrice = it.salePrice,
                    createdAt = it.createdAt
                )
            }
        )
    }
    fun getAll(): List<ProductSugestionRequest> {
        return repository.findAllByOrderByIdDesc().map { suggestion ->
            val createdByUsername = suggestion.createdBy?.let { userService.findUserById(it)?.username }
            val updatedByUsername = suggestion.updatedBy?.let { userService.findUserById(it)?.username }
            val lines = productSugestionLineService.findByProductSugestion(suggestion)
            val productImageBase64 = suggestion.productImage?.let { Base64.getEncoder().encodeToString(it) }

            ProductSugestionRequest(
                id = suggestion.id,
                name = suggestion.name,
                description = suggestion.description,
                status = suggestion.status,
                productImage = productImageBase64,
                costPrice = suggestion.costPrice,
                salePrice = suggestion.salePrice,
                supplierId = suggestion.supplierId,
                justification = suggestion.justification,
                sector = suggestion.sector,
                isProductLine = suggestion.isProductLine,
                createdAt = suggestion.createdAt,
                lines = lines.map {
                    ProductSugestionLineResponse(
                        id = it.id,
                        name = it.name,
                        costPrice = it.costPrice,
                        salePrice = it.salePrice,
                        createdAt = it.createdAt
                    )
                },
                createdBy = suggestion.createdBy,
                updatedBy = suggestion.updatedBy,
                createdByUsername = createdByUsername,
                updatedByUsername = updatedByUsername
            )
        }
    }

    @Transactional
    fun update(
        id: Long,
        request: ProductSugestionRequest,
        productImage: MultipartFile?,
        productLines: List<ProductSugestionLineRequest> = emptyList()
    ): ProductSugestionModel? {
        val authUser = userService.getAuthUser()
        val existing = repository.findById(id).orElse(null) ?: return null
        val userStoreWhoCreated = existing.createdBy?.let { userService.findUserById(it)?.store }
        print(authUser)
        val updated = existing.copy(
            name = request.name,
            description = request.description,
            status = request.status,
            productImage = productImage?.bytes ?: existing.productImage,
            costPrice = request.costPrice ?: existing.costPrice,
            salePrice = request.salePrice ?: existing.salePrice,
            supplierId = request.supplierId ?: existing.supplierId,
            justification = request.justification ?: existing.justification,
            sector = request.sector,
            updatedBy = authUser?.id
        )
        val saved = repository.save(updated)
        // val lines = productSugestionLineService.findByProductSugestion(saved)
        val fornecedor = supplierService.findById(request.supplierId ?: 0L)?.name ?: "Fornecedor não encontrado"
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        val produtos = if (request.isProductLine) {
            "Produtos / Custos:\n" + productLines.joinToString("\n") {
                val preco = it.costPrice?.let { v -> numberFormat.format(v) } ?: "-"
                "${it.name} / $preco"
            }
        } else {
            val preco = request.costPrice?.let { v -> numberFormat.format(v) } ?: "-"
            "Custo: $preco"
        }

        if (request.isProductLine) {
            productSugestionLineService.saveLines(saved, productLines)
        }

        val msg = """
            Segue tratativa de Nova Sugestão de Produtos, para aprovações:
            Loja Solicitante: ${userStoreWhoCreated ?: "N/A"}
            Produto: ${request.name}
            Linha Completa: ${if (request.isProductLine) "Sim" else "Não"}
            Descrição: ${request.description ?: "Nenhuma descrição fornecida"}
            Fornecedor: $fornecedor
            $produtos
        """.trimIndent().replace(Regex("^ +", RegexOption.MULTILINE), "")

        chatsacService.sendMsg(
            number = "663a53e93b0a671bbcb23c93",
            message = msg,
        ).subscribe()
        val productImage = existing.productImage ?: productImage
        // Envia a imagem do produto sugerido, se existir
        val imageBytes: ByteArray? = when {
            productImage is MultipartFile -> productImage.bytes
            productImage is ByteArray -> productImage
            else -> null
        }
        if (imageBytes != null) {
            chatsacService.sendImg(
                imageBytes = imageBytes,
                fileName = "produto_sugerido.jpg",
                number = "663a53e93b0a671bbcb23c93",
                caption = "Imagem do produto sugerido: ${request.name}"
            ).subscribe()
        }

        print(msg)
        return saved
    }

    // fun delete(id: Long) {
    //     repository.deleteById(id)
    // }
}