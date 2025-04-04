package agoraa.app.forms_back.extra_quotations.extra_quotation_products.repository

import agoraa.app.forms_back.extra_quotations.extra_quotation_products.model.ExtraQuotationProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraQuotationProductsRepository : JpaRepository<ExtraQuotationProductsModel, Long> {
    fun findByExtraQuotationId(extraQuotationId: Long): List<ExtraQuotationProductsModel>
}