package agoraa.app.forms_back.survery.repository

import agoraa.app.forms_back.survery.model.QuestionModel
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<QuestionModel, Long> {
    @EntityGraph(attributePaths = ["options"])
    fun findAllBySurveyId(surveyId: Long): List<QuestionModel>
}