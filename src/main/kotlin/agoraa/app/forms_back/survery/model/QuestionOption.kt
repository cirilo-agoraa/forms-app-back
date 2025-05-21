package agoraa.app.forms_back.survery.model

import jakarta.persistence.*

@Entity
@Table(name = "question_options")
data class QuestionOptionModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val optionText: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: QuestionModel
)