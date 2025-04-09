package agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.service

import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.request.WeeklyQuotationSummariesRequest
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.response.WeeklyQuotationSummariesResponse
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.model.WeeklyQuotationSummariesModel
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.repository.WeeklyQuotationSummariesRepository
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.model.WeeklyQuotationModel
import org.springframework.stereotype.Service

@Service
class WeeklyQuotationSummariesService(private val weeklyQuotationSummaryRepository: WeeklyQuotationSummariesRepository) {
    private fun create(
        weeklyQuotation: WeeklyQuotationModel,
        summaries: List<WeeklyQuotationSummariesRequest>
    ) {
        val weeklyQuotationSummaryModels = summaries.map { p ->
            WeeklyQuotationSummariesModel(
                weeklyQuotation = weeklyQuotation,
                situation = p.situation,
                quantity = p.quantity,
            )
        }
        weeklyQuotationSummaryRepository.saveAll(weeklyQuotationSummaryModels)
    }

    private fun edit(summaries: List<WeeklyQuotationSummariesModel>, request: List<WeeklyQuotationSummariesRequest>) {
        val situationsMap = request.associateBy { it.situation }
        val weeklyQuotationSummaryModels = summaries.map { p ->
            val requestProduct = situationsMap[p.situation]!!
            p.copy(
                quantity = requestProduct.quantity,
            )
        }
        weeklyQuotationSummaryRepository.saveAll(weeklyQuotationSummaryModels)
    }

    fun findByParentId(
        weeklyQuotationId: Long,
    ): List<WeeklyQuotationSummariesModel> = weeklyQuotationSummaryRepository.findByWeeklyQuotationId(weeklyQuotationId)

    fun createDto(
        weeklyQuotationSummaryModel: WeeklyQuotationSummariesModel,
    ): WeeklyQuotationSummariesResponse {
        return WeeklyQuotationSummariesResponse(
            id = weeklyQuotationSummaryModel.id,
            quantity = weeklyQuotationSummaryModel.quantity,
            situation = weeklyQuotationSummaryModel.situation
        )
    }

    fun editOrCreateOrDelete(
        weeklyQuotation: WeeklyQuotationModel,
        summaries: List<WeeklyQuotationSummariesRequest>
    ) {
        val weeklyQuotationSummariesModels = findByParentId(weeklyQuotation.id)
        val currentWeeklyQuotationSummariesSet = weeklyQuotationSummariesModels.map { it.situation }.toSet()
        val newWeeklyQuotationSummariesSet = summaries.map { it.situation }.toSet()

        val toAdd = summaries.filter { it.situation !in currentWeeklyQuotationSummariesSet }
        create(weeklyQuotation, toAdd)

        val toEdit = weeklyQuotationSummariesModels.filter { it.situation in newWeeklyQuotationSummariesSet }
        edit(toEdit, summaries)
    }
}