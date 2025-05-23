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
import agoraa.app.forms_back.survery.dto.SurveyAnswerHistoryResponse
import agoraa.app.forms_back.survery.dto.SurveyAnsweredResponse
import agoraa.app.forms_back.survery.dto.QuestionAnsweredResponse

import agoraa.app.forms_back.users.users.repository.UserRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Service
class SurveyService(private val repository: SurveyRepository,
                    private val questionRepository: QuestionRepository,
                    private val questionOptionRepository: QuestionOptionRepository,
                    private val answerRepository: AnswersRepository,
                    private val userRepository: UserRepository
            

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
            description = request.description,
            isAnonimous = request.isAnonimous
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
        val surveys = repository.findAll()
        return surveys.map { survey ->
            val wasAnswered = answerRepository.wasSurveyAnsweredByUser(survey.id, userId)
            SurveyUserResponse(
                id = survey.id,
                title = survey.title,
                description = survey.description,
                wasAnswered = wasAnswered ,
                isAnonimous = if (survey.isAnonimous) "Sim" else "Não"            )
        }
    }

    fun saveSurveyResponses(request: SurveyAnswerRequest) {
        // Busca o maior answer_index atual
        val lastIndex = answerRepository.findMaxAnswerIndex() ?: 0
        val newIndex = lastIndex + 1

        request.respostas.forEach { resposta ->
            val question = questionRepository.findById(resposta.questionId)
                .orElseThrow { NoSuchElementException("Question not found") }
            val survey = repository.findById(question.survey.id)
                .orElseThrow { NoSuchElementException("Survey not found") }
            val isAnonimous = survey.isAnonimous
            val answer = AnswerModel(
                question = question,
                response = resposta.answer.toString(),
                userId = if (isAnonimous) null else request.userId,
                answerIndex = newIndex
            )
            answerRepository.save(answer)
        }
    }

    // fun getAnswerHistoryBySurveyId(surveyId: Long): List<Map<String, Any?>> {
    //     val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale("pt", "BR"))
    //     println("DEBUG surveyId: $surveyId")

    //     val survey = repository.findById(surveyId).orElseThrow { NoSuchElementException("Survey not found") }
    //     val questions = questionRepository.findAllBySurveyId(survey.id)
    //     val answers = answerRepository.findAll()
    //         .filter { it.question.survey.id == surveyId }

    //     val grouped = answers.groupBy { it.answerIndex }

    //     return grouped.values.sortedByDescending { group -> group.firstOrNull()?.createdAt }
    //         .map { group ->
    //             val createdAt = group.firstOrNull()?.createdAt?.format(formatter)
    //             val userId = group.firstOrNull()?.userId
    //             val userName = userId?.let { userRepository.findById(it).orElse(null)?.username }
    //             // Aqui cada resposta vira um par: "titulo da pergunta" -> resposta
    //             val respostas = questions.associate { question ->
    //                 question.title to group.find { it.question.id == question.id }?.response
    //             }
    //             mapOf(
    //                 "answeredAt" to createdAt,
    //                 "userId" to userId,
    //                 "userName" to userName,
    //                 "answers" to respostas
    //             )
    //         }
    // }

    fun getAnswerHistoryBySurveyId(surveyId: Long): List<Map<String, Any?>> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale("pt", "BR"))
        println("DEBUG surveyId: $surveyId")

        val survey = repository.findById(surveyId).orElseThrow { NoSuchElementException("Survey not found") }
        val surveyTitle = survey.title
        val questions = questionRepository.findAllBySurveyId(survey.id)
        val answers = answerRepository.findAll()
            .filter { it.question.survey.id == surveyId }

        val grouped = answers.groupBy { it.answerIndex }

        return grouped.values.sortedByDescending { group -> group.firstOrNull()?.createdAt }
            .map { group ->
                val createdAt = group.firstOrNull()?.createdAt?.format(formatter)
                val userId = group.firstOrNull()?.userId
                val userName = userId?.let { userRepository.findById(it).orElse(null)?.username }
                // Aqui cada resposta vira um par: "titulo da pergunta" -> resposta
                val respostas = questions.associate { question ->
                    question.title to group.find { it.question.id == question.id }?.response
                }
                mapOf(
                    "surveyTitle" to surveyTitle,
                    "answeredAt" to createdAt,
                    "userId" to userId,
                    "userName" to userName,
                    "answers" to respostas
                )
            }
    }

    fun getSurveyWithUserResponses(surveyId: Long, userId: Long): SurveyAnsweredResponse {
        val survey = repository.findById(surveyId).orElseThrow { NoSuchElementException("Survey not found") }
        val questions = questionRepository.findAllBySurveyId(survey.id)
        val answers = answerRepository.findAllByUserIdAndSurveyId(userId, surveyId)
            .associateBy { it.question.id }

        return SurveyAnsweredResponse(
            id = survey.id,
            title = survey.title,
            description = survey.description,
            isAnonimous = survey.isAnonimous,
            questions = questions.map { question ->
                QuestionAnsweredResponse(
                    id = question.id,
                    title = question.title,
                    typeOfQuestion = question.typeOfQuestion.toString(),
                    response = answers[question.id]?.response,
                    required = question.required
                )
            }
        )
    }


    // fun delete(id: Long) {
    //     repository.deleteById(id)
    // }
}