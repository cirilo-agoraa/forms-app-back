package agoraa.app.forms_back.survery.dto

data class SurveyWithQuestionsResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val isAnonimous: Boolean,
    val questions: List<QuestionResponse>
)

data class SurveyRequest(
    val title: String,
    val description: String?,
    val isAnonimous: Boolean,
    val questions: List<QuestionRequest>
)

data class QuestionRequest(
    val text: String,
    val type: String,
    val options: List<String> = emptyList(),
    val required: Boolean
)

data class QuestionResponse(
    val id: Long,
    val title: String,
    val typeOfQuestion: String,
    val options: List<QuestionOptionResponse> = emptyList(),
    val required: Boolean
)

data class SurveyResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val isAnonimous: Boolean = false

)


data class QuestionOptionResponse(
    val id: Long,
    val text: String
)

data class QuestionOptionRequest(
    val text: String
)

data class SurveyUserResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val wasAnswered: Boolean
)

data class SurveyAnswerRequest(
    val userId: Long,
    val respostas: List<QuestionAnswerRequest>
)

data class QuestionAnswerRequest(
    val questionId: Long,
    val answer: String
)

data class SurveyAnswerHistoryResponse(
    val surveyId: Long,
    val surveyTitle: String,
    val userId: Long?,
    val answeredAt: String,
    val userName: String?,
)

data class QuestionAnsweredResponse(
    val id: Long,
    val title: String,
    val typeOfQuestion: String,
    val response: String?,
    val required: Boolean
)

data class SurveyAnsweredResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val isAnonimous: Boolean,
    val questions: List<QuestionAnsweredResponse>
)