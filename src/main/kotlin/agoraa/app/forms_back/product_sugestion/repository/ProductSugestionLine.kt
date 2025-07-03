package agoraa.app.forms_back.product_sugestion.repository

import agoraa.app.forms_back.product_sugestion.model.ProductSugestionLine
import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSugestionLineRepository : JpaRepository<ProductSugestionLine, Long> {
    fun findByProductSugestion(productSugestion: ProductSugestionModel): List<ProductSugestionLine>
    fun deleteByProductSugestion(productSugestion: ProductSugestionModel)
}