package agoraa.app.forms_back.passive_quotations.passive_quotation_products.repository

import agoraa.app.forms_back.passive_quotations.passive_quotation_products.model.PassiveQuotationProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PassiveQuotationProductsRepository: JpaRepository<PassiveQuotationProductsModel, Long> {
    fun findByPassiveQuotationId(passiveQuotationId: Long): List<PassiveQuotationProductsModel>
}