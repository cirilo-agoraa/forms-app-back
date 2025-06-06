package agoraa.app.forms_back.shared.service

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

    fun sendMsg(message: String, number: String): Mono<String> {
        val body = mapOf(
            "number" to number, // Exemplo: "5527999000862"
            "message" to message,
            "isWhisper" to false,
            "forceSend" to true,
            "verifyContact" to false,
            "delayInSeconds" to 0
        )

        val response = webClient.post()
            .uri("/send-text")
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

        return response
    }

    fun sendImg(imageBytes: ByteArray, fileName: String, number: String): Mono<String> {
    val fileBase64 = java.util.Base64.getEncoder().encodeToString(imageBytes)
    val body = mapOf(
        "base64" to fileBase64,
        "extension" to ".jpg", // ou ".png" se preferir
        "fileName" to fileName,
        "contactId" to number,
        "forceSend" to true,
        "verifyContact" to true
    )

        return webClient.post()
            .uri("/send-media")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorResume { error ->
                if (error is org.springframework.web.reactive.function.client.WebClientResponseException) {
                    val errorBody = error.responseBodyAsString
                    Mono.just(errorBody.ifEmpty { "Error: ${error.message}" })
                } else {
                    Mono.just("Error: ${error.message}")
                }
            }
    }
}