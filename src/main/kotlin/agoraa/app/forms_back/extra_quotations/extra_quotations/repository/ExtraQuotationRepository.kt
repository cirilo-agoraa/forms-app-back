package agoraa.app.forms_back.extra_quotations.extra_quotations.repository

import agoraa.app.forms_back.extra_quotations.extra_quotations.model.ExtraQuotationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraQuotationRepository : JpaRepository<ExtraQuotationModel, Long>,
    JpaSpecificationExecutor<ExtraQuotationModel> {
}