package agoraa.app.forms_back.passive_quotations.passive_quotations.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PassiveQuotationRepository: JpaRepository<agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel, Long>, JpaSpecificationExecutor<agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel> {
}