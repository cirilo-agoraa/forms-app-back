package agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.repository

import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.model.WeeklyQuotationSummariesModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WeeklyQuotationSummariesRepository: JpaRepository<WeeklyQuotationSummariesModel, Long> {
    fun findByWeeklyQuotationId(weeklyQuotationId: Long): List<WeeklyQuotationSummariesModel>
}
