package agoraa.app.forms_back.survery.repository

import agoraa.app.forms_back.survery.model.QuestionOptionModel
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionOptionRepository : JpaRepository<QuestionOptionModel, Long>