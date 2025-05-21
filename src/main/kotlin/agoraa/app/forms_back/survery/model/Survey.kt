package agoraa.app.forms_back.survery.model

import jakarta.persistence.*

@Entity
@Table(name = "surveys")
data class SurveyModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = false)
    val isAnonimous: Boolean = false,

    @OneToMany(mappedBy = "survey", cascade = [CascadeType.ALL], orphanRemoval = true)
    val questions: List<QuestionModel> = emptyList()
)