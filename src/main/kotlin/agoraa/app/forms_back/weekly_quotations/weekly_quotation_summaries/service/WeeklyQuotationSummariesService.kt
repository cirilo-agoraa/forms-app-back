package agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.service

import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.request.WeeklyQuotationSummariesRequest
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.response.WeeklyQuotationSummariesAnalysisResponse
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.response.WeeklyQuotationSummariesResponse
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.enums.WeeklyQuotationSummariesSituationEnum
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
                percentage = p.percentage
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
                percentage = requestProduct.percentage
            )
        }
        weeklyQuotationSummaryRepository.saveAll(weeklyQuotationSummaryModels)
    }

    fun findByParentId(
        weeklyQuotationId: Long,
    ): List<WeeklyQuotationSummariesModel> = weeklyQuotationSummaryRepository.findByWeeklyQuotationId(weeklyQuotationId)

    fun summaryAnalysis(sector: ProductSectorsEnum): List<WeeklyQuotationSummariesAnalysisResponse> {
        val weeklyQuotationSummariesModels = weeklyQuotationSummaryRepository.findAll()
        val groupedBySituation = weeklyQuotationSummariesModels.groupBy { it.situation }

        val qtdeProdutosEnviadosParaCotacaoAll =
            groupedBySituation[WeeklyQuotationSummariesSituationEnum.QTDE_PRODUTOS_ENVIADOS_PARA_COTACAO].orEmpty()
        val qtdeProdutosCotadosAll =
            groupedBySituation[WeeklyQuotationSummariesSituationEnum.QTDE_PRODUTOS_COTADOS].orEmpty()

        val qtdeProdutosEnviadosParaCotacaoAllTotal =
            if (qtdeProdutosEnviadosParaCotacaoAll.isNotEmpty()) qtdeProdutosEnviadosParaCotacaoAll.sumOf {
                it.percentage ?: 0.0
            } / qtdeProdutosEnviadosParaCotacaoAll.size else 0.0
        val qtdeProdutosCotadosAllTotal = if (qtdeProdutosCotadosAll.isNotEmpty()) qtdeProdutosCotadosAll.sumOf {
            it.percentage ?: 0.0
        } / qtdeProdutosCotadosAll.size else 0.0

        val qtdeProdutosEnviadosParaCotacaoSector =
            qtdeProdutosEnviadosParaCotacaoAll.filter { it.weeklyQuotation.sector == sector }
        val qtdeProdutosCotadosSector = qtdeProdutosCotadosAll.filter { it.weeklyQuotation.sector == sector }

        val qtdeProdutosEnviadosParaCotacaoSectorTotal =
            if (qtdeProdutosEnviadosParaCotacaoSector.isNotEmpty()) qtdeProdutosEnviadosParaCotacaoSector.sumOf {
                it.percentage ?: 0.0
            } / qtdeProdutosEnviadosParaCotacaoAll.size else 0.0
        val qtdeProdutosCotadosSectorTotal =
            if (qtdeProdutosCotadosSector.isNotEmpty()) qtdeProdutosCotadosSector.sumOf {
                it.percentage ?: 0.0
            } / qtdeProdutosCotadosAll.size else 0.0

        return listOf(
            WeeklyQuotationSummariesAnalysisResponse(
                situation = WeeklyQuotationSummariesSituationEnum.QTDE_PRODUTOS_ENVIADOS_PARA_COTACAO,
                percentageAll = qtdeProdutosEnviadosParaCotacaoAllTotal,
                percentageSector = qtdeProdutosEnviadosParaCotacaoSectorTotal
            ),
            WeeklyQuotationSummariesAnalysisResponse(
                situation = WeeklyQuotationSummariesSituationEnum.QTDE_PRODUTOS_COTADOS,
                percentageAll = qtdeProdutosCotadosAllTotal,
                percentageSector = qtdeProdutosCotadosSectorTotal
            )
        )
    }

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