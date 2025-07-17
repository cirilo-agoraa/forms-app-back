package agoraa.app.forms_back.products_resume.repository

import agoraa.app.forms_back.products_resume.model.ProductsResumeModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductsResumeRepository : JpaRepository<ProductsResumeModel, Long> {
    fun findByCode(code: String): ProductsResumeModel?
}