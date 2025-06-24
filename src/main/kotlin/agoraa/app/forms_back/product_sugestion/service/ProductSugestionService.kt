package agoraa.app.forms_back.product_sugestion.service

import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionRequest
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import agoraa.app.forms_back.product_sugestion.repository.ProductSugestionRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import agoraa.app.forms_back.shared.service.ChatsacService

@Service
class ProductSugestionService(
    private val repository: ProductSugestionRepository,
    private val chatsacService: ChatsacService
) {
    fun create(request: ProductSugestionRequest, productImage: MultipartFile?): ProductSugestionModel {


        val entity = ProductSugestionModel(
            name = request.name,
            description = request.description,
            productImage = productImage?.bytes
        )
        val resp =  repository.save(entity)

        val msg = """
            Solicitação de cadastro de produto:
            • Nome: ${request.name}
            • Descrição: ${request.description ?: "Nenhuma descrição fornecida"}
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

    fun getById(id: Long): ProductSugestionModel? = repository.findById(id).orElse(null)
    fun getAll(): List<ProductSugestionModel> = repository.findAll()

    fun update(id: Long, request: ProductSugestionRequest, productImage: MultipartFile?): ProductSugestionModel? {
        println("Updating product suggestion with ID: $id")
        println("Request data: $request")
        val existing = repository.findById(id).orElse(null) ?: return null
        val updated = existing.copy(
            name = request.name,
            description = request.description,
            status = request.status,
            productImage = productImage?.bytes ?: existing.productImage,
            costPrice = request.costPrice ?: existing.costPrice,
            salePrice = request.salePrice ?: existing.salePrice,
            supplierId = request.supplierId ?: existing.supplierId,
            justification = request.justification ?: existing.justification,
            sector = request.sector // Assuming sector is not in the request, keep existing value
        )
        return repository.save(updated)
    }
}