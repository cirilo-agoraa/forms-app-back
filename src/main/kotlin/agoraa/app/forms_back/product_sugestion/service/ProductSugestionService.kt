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
            number = "27999000862",
            message = msg,
        ).subscribe()

        if (productImage != null) {
            chatsacService.sendImg(
                imageBytes = productImage.bytes,
                fileName = "produto_sugerido.jpg",
                number = "27999000862",
                caption = "Imagem do produto sugerido: ${request.name}"
            ).subscribe()
        }

        return resp
    }

    fun getById(id: Long): ProductSugestionModel? = repository.findById(id).orElse(null)
    fun getAll(): List<ProductSugestionModel> = repository.findAll()

    fun update(id: Long, request: ProductSugestionRequest, productImage: MultipartFile?): ProductSugestionModel? {
        val existing = repository.findById(id).orElse(null) ?: return null
        val updated = existing.copy(
            name = request.name,
            description = request.description,
            status = request.status,
            productImage = productImage?.bytes ?: existing.productImage
        )
        return repository.save(updated)
    }
}