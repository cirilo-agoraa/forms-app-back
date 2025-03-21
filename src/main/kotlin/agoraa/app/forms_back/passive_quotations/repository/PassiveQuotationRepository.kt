package agoraa.app.forms_back.passive_quotations.repository

import agoraa.app.forms_back.passive_quotations.model.PassiveQuotationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PassiveQuotationRepository: JpaRepository<PassiveQuotationModel, Long>, JpaSpecificationExecutor<PassiveQuotationModel> {
}