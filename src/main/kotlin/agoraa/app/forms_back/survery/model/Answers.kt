package agoraa.app.forms_back.survery.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "answers")
data class AnswerModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: QuestionModel,

    @Column(nullable = false)
    val response: String,

    @Column(name = "user_id", nullable = true)
    val userId: Long? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)