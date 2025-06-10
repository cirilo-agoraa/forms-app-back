package agoraa.app.forms_back.product_sugestion.repository

import agoraa.app.forms_back.product_sugestion.model.ProductSugestionModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSugestionRepository : JpaRepository<ProductSugestionModel, Long>