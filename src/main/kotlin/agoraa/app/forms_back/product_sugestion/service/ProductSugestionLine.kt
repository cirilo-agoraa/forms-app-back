package agoraa.app.forms_back.product_sugestion.service

import agoraa.app.forms_back.product_sugestion.dto.ProductSugestionLineRequest
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionLine
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import agoraa.app.forms_back.product_sugestion.repository.ProductSugestionLineRepository
import org.springframework.stereotype.Service

@Service
class ProductSugestionLineService(
    private val repository: ProductSugestionLineRepository
) {
    fun saveLines(
        productSugestion: ProductSugestionModel,
        lines: List<ProductSugestionLineRequest>
    ) {
        // Remove linhas antigas
        repository.deleteByProductSugestion(productSugestion)
        // Salva novas linhas
        lines.forEach { req ->
            repository.save(
                ProductSugestionLine(
                    name = req.name,
                    costPrice = req.costPrice,
                    salePrice = req.salePrice,
                    productSugestion = productSugestion
                )
            )
        }
    }

    fun findByProductSugestion(productSugestion: ProductSugestionModel): List<ProductSugestionLine> =
        repository.findByProductSugestion(productSugestion)
}