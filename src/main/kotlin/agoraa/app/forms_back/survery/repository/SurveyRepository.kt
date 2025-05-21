package agoraa.app.forms_back.survery.repository

import agoraa.app.forms_back.survery.model.SurveyModel
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface SurveyRepository : JpaRepository<SurveyModel, Long> {
    @EntityGraph(attributePaths = ["questions"])
    override fun findAll(): List<SurveyModel>
    // fun existsByQuestionSurveyIdAndUserId(surveyId: Long, userId: Long): Boolean
    // fun findAllByIsAnonimousFalse(): List<SurveyModel>    
    fun findAllByIsAnonimousFalse(): List<SurveyModel>
}