package agoraa.app.forms_back.passive_quotations.repository

import agoraa.app.forms_back.passive_quotations.model.PassiveQuotationProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PassiveQuotationProductsRepository: JpaRepository<PassiveQuotationProductsModel, Long> {
    fun findByPassiveQuotationId(passiveQuotationId: Long): List<PassiveQuotationProductsModel>
}