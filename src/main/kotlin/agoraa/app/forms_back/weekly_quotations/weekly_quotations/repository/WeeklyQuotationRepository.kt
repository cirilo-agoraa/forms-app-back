package agoraa.app.forms_back.weekly_quotations.weekly_quotations.repository

import agoraa.app.forms_back.weekly_quotations.weekly_quotations.model.WeeklyQuotationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface WeeklyQuotationRepository: JpaRepository<WeeklyQuotationModel, Long>, JpaSpecificationExecutor<WeeklyQuotationModel> {
}