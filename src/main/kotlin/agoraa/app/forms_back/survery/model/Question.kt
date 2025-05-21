package agoraa.app.forms_back.survery.model

import jakarta.persistence.*

@Entity
@Table(name = "questions")
data class QuestionModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val typeOfQuestion: Int,

    @Column(nullable = false)
    val required: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    val survey: SurveyModel,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    val options: List<QuestionOptionModel> = emptyList()
)