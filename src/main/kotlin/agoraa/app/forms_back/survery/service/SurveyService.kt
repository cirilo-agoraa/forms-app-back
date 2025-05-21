package agoraa.app.forms_back.survery.service
import agoraa.app.forms_back.survery.dto.SurveyRequest
import agoraa.app.forms_back.survery.dto.SurveyResponse
import agoraa.app.forms_back.survery.dto.SurveyUserResponse
import agoraa.app.forms_back.survery.dto.SurveyWithQuestionsResponse
import agoraa.app.forms_back.survery.dto.QuestionResponse
import agoraa.app.forms_back.survery.dto.QuestionOptionResponse
import agoraa.app.forms_back.survery.dto.QuestionRequest
import agoraa.app.forms_back.survery.model.SurveyModel
import agoraa.app.forms_back.survery.repository.SurveyRepository
import org.springframework.stereotype.Service
import agoraa.app.forms_back.survery.model.QuestionOptionModel
import agoraa.app.forms_back.survery.model.QuestionModel
import agoraa.app.forms_back.survery.repository.QuestionRepository
import agoraa.app.forms_back.survery.repository.QuestionOptionRepository
import agoraa.app.forms_back.survery.repository.AnswersRepository
import agoraa.app.forms_back.survery.model.AnswerModel
import agoraa.app.forms_back.survery.dto.SurveyAnswerRequest


@Service
class SurveyService(private val repository: SurveyRepository,
                    private val questionRepository: QuestionRepository,
                    private val questionOptionRepository: QuestionOptionRepository,
                    private val answerRepository: AnswersRepository

) {

    fun findAll(): List<SurveyResponse> =
        repository.findAll().map { survey ->
            SurveyResponse(
                id = survey.id,
                title = survey.title,
                description = survey.description
            )
        }
    
    
    fun findAllWithQuestions(): List<SurveyWithQuestionsResponse> =
        repository.findAll().map { survey ->
            SurveyWithQuestionsResponse(
                id = survey.id,
                title = survey.title,
                description = survey.description,
                isAnonimous = survey.isAnonimous,
                questions = survey.questions.map { question ->
                    QuestionResponse(
                        id = question.id,
                        title = question.title,
                        typeOfQuestion = question.typeOfQuestion.toString(),
                        required = question.required
                    )
                }
            )
        }
    

    fun findSurveyById(id: Long): SurveyWithQuestionsResponse {
        val survey = repository.findById(id).orElseThrow { NoSuchElementException("Survey not found") }
        val questions = questionRepository.findAllBySurveyId(survey.id)
        return SurveyWithQuestionsResponse(
            id = survey.id,
            title = survey.title,
            description = survey.description,
            isAnonimous = survey.isAnonimous,
            questions = questions.map { question ->
                QuestionResponse(
                    id = question.id,
                    title = question.title,
                    typeOfQuestion = question.typeOfQuestion.toString(),
                    required = question.required,
                    options = question.options.map { option ->
                        QuestionOptionResponse(
                            id = option.id,
                            text = option.optionText
                        )
                    }
                )
            }
        )
    }

    fun create(request: SurveyRequest): SurveyResponse {
        require(request.questions.isNotEmpty()) { "O survey deve ter pelo menos uma pergunta." }

        val survey = SurveyModel(
            title = request.title,
            description = request.description
        )
        val savedSurvey = repository.save(survey)

        request.questions.forEach { q ->
            val questionModel = QuestionModel(
                title = q.text,
                typeOfQuestion = when (q.type) {
                    "multiple-choice" -> 1 
                    "text" -> 2
                    else -> 0
                },
                required = q.required,
                survey = savedSurvey
            )
            val savedQuestion = questionRepository.save(questionModel)

            if (q.type == "multiple-choice") {
                q.options.forEach { optionText ->
                    val option = QuestionOptionModel(
                        optionText = optionText,
                        question = savedQuestion
                    )
                    questionOptionRepository.save(option)
                }
            }
        }

        return SurveyResponse(savedSurvey.id, savedSurvey.title, savedSurvey.description)
    }

    fun update(id: Long, request: SurveyRequest): SurveyResponse {
        val survey = repository.findById(id).orElseThrow { NoSuchElementException("Survey not found") }
        val updated = survey.copy(title = request.title, description = request.description)
        repository.save(updated)
        return SurveyResponse(updated.id, updated.title, updated.description)
    }

    fun findAllByLoggedUser(userId: Long): List<SurveyUserResponse> {
        val surveys = repository.findAllByIsAnonimousFalse()
        return surveys.map { survey ->
            val wasAnswered = answerRepository.wasSurveyAnsweredByUser(survey.id, userId)
            SurveyUserResponse(
                id = survey.id,
                title = survey.title,
                description = survey.description,
                wasAnswered = wasAnswered 
            )
        }
    }

    fun saveSurveyResponses(request: SurveyAnswerRequest) {
        request.respostas.forEach { resposta ->
            val question = questionRepository.findById(resposta.questionId)
                .orElseThrow { NoSuchElementException("Question not found") }
            val answer = AnswerModel(
                question = question,
                response = resposta.answer.toString(),
                userId = request.userId
            )
            answerRepository.save(answer)
            
        }
    }

    // fun delete(id: Long) {
    //     repository.deleteById(id)
    // }
}