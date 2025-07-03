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

@Service
class ProductSugestionService(
    private val repository: ProductSugestionRepository,
    private val chatsacService: ChatsacService,
    private val productSugestionLineService: ProductSugestionLineService // adicione esta linha

) {
    fun create(request: ProductSugestionRequest, productImage: MultipartFile?): ProductSugestionModel {


        val entity = ProductSugestionModel(
            name = request.name,
            description = request.description,
            isProductLine = request.isProductLine,
            productImage = productImage?.bytes
        )
        val resp =  repository.save(entity)

        val msg = """
            Solicitação de cadastro de produto:
            • Nome: ${request.name}
            • Descrição: ${request.description ?: "Nenhuma descrição fornecida"}
        """.trimIndent()

        // chatsacService.sendMsg(
        //     number = "663a53e93b0a671bbcb23c93",
        //     message = msg,
        // ).subscribe()

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
        return ProductSugestionRequest(
            name = suggestion.name,
            description = suggestion.description,
            status = suggestion.status,
            productImage = null, // ou converta se precisar
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
    fun getAll(): List<ProductSugestionModel> = repository.findAll()

    @Transactional
    fun update(
        id: Long,
        request: ProductSugestionRequest,
        productImage: MultipartFile?,
        productLines: List<ProductSugestionLineRequest> = emptyList()
    ): ProductSugestionModel? {
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
            sector = request.sector
        )
        val saved = repository.save(updated)

        if (request.isProductLine) {
            productSugestionLineService.saveLines(saved, productLines)
        }

        return saved
    }

    // fun delete(id: Long) {
    //     repository.deleteById(id)
    // }
}