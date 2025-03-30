package agoraa.app.forms_back.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Service
class ChatsacService {
    private val botToken: String = io.github.cdimascio.dotenv.dotenv().get("BOT_TOKEN")
    private val baseUrl: String = "https://api.chatsac.com/core/v2/api/chats"
    private val headers: Map<String, String> = mapOf(
        "access-token" to botToken
    )
    private val webClient: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeaders { it.setAll(headers) }
        .build()

    fun sendPdf(filePath: String, groupId: String): Mono<String> {
        val file = java.io.File(filePath)
        val fileBytes = file.readBytes()
        val fileBase64 = java.util.Base64.getEncoder().encodeToString(fileBytes)

        val body = mapOf(
            "base64" to fileBase64,
            "extension" to "." + file.extension,
            "fileName" to file.name.replace(".pdf", ""),
            "contactId" to groupId,
            "forceSend" to true,
            "verifyContact" to true,
        )

        val response =  webClient.post()
            .uri("/send-media")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorResume { error ->
                if (error is WebClientResponseException) {
                    val errorBody = error.responseBodyAsString
                    Mono.just(errorBody.ifEmpty { "Error: ${error.message}" })
                } else {
                    Mono.just("Error: ${error.message}")
                }
            }

        file.delete()

        return response
    }
}