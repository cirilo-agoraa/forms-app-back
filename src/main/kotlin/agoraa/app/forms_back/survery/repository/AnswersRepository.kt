package agoraa.app.forms_back.survery.repository

import agoraa.app.forms_back.survery.model.AnswerModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.jpa.repository.EntityGraph

interface AnswersRepository : JpaRepository<AnswerModel, Long> {
    @Query("""
        SELECT COUNT(a) > 0 FROM AnswerModel a
        WHERE a.question.survey.id = :surveyId AND a.userId = :userId
    """)
    fun wasSurveyAnsweredByUser(@Param("surveyId") surveyId: Long, @Param("userId") userId: Long): Boolean

    @EntityGraph(attributePaths = ["question", "question.survey"])
    override fun findAll(): List<AnswerModel>

    @Query("""
        SELECT a FROM AnswerModel a
        WHERE a.userId = :userId AND a.question.survey.id = :surveyId
    """)
    @EntityGraph(attributePaths = ["question", "question.survey"])
    fun findAllByUserIdAndSurveyId(
        @Param("userId") userId: Long,
        @Param("surveyId") surveyId: Long
    ): List<AnswerModel>
}