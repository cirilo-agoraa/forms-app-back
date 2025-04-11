package agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.model

import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.enums.WeeklyQuotationSummariesSituationEnum
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.model.WeeklyQuotationModel
import jakarta.persistence.*

@Entity
@Table(name = "weekly_quotation_summaries")
data class WeeklyQuotationSummariesModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "weekly_quotation_id")
    val weeklyQuotation: WeeklyQuotationModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val situation: WeeklyQuotationSummariesSituationEnum,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = true)
    val percentage: Double? = null,
)
