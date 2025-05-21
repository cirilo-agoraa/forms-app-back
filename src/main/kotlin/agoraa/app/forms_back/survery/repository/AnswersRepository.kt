package agoraa.app.forms_back.survery.repository

import agoraa.app.forms_back.survery.model.AnswerModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AnswersRepository : JpaRepository<AnswerModel, Long> {
    @Query("""
        SELECT COUNT(a) > 0 FROM AnswerModel a
        WHERE a.question.survey.id = :surveyId AND a.userId = :userId
    """)
    fun wasSurveyAnsweredByUser(@Param("surveyId") surveyId: Long, @Param("userId") userId: Long): Boolean
}