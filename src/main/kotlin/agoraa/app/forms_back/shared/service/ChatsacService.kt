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
            "number" to number,
            "message" to message,
            "isWhisper" to false,
            "forceSend" to true,
            "verifyContact" to false,
            "delayInSeconds" to 0
        )
        println("BOT_TOKEN usado: $botToken")

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

    fun sendImg(imageBytes: ByteArray, fileName: String, number: String, caption: String? = null): Mono<String> {
        val fileBase64 = java.util.Base64.getEncoder().encodeToString(imageBytes)
        val body = mapOf(
            "base64" to fileBase64,
            "extension" to ".jpg",
            "fileName" to fileName,
            "contactId" to number,
            "forceSend" to true,
            "caption" to (caption ?: ""),
            "verifyContact" to true
        )

        return webClient.post()
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
    }

    fun sendMessage(message: String, number: String): Mono<String> {
    val body = mapOf(
        "number" to number,
        "message" to message,
        "isWhisper" to false,
        "forceSend" to true,
        "verifyContact" to false,
        "delayInSeconds" to 0
    )
    println("BOT_TOKEN usado: $botToken")
    println("Corpo enviado: $body")

    return webClient.post()
        .uri("/send-text")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String::class.java)
        .doOnNext { println("Resposta da API: $it") }
        .doOnError { println("Erro ao enviar: ${it.message}") }
        .onErrorResume { error ->
            if (error is WebClientResponseException) {
                val errorBody = error.responseBodyAsString
                Mono.just(errorBody.ifEmpty { "Error: ${error.message}" })
            } else {
                Mono.just("Error: ${error.message}")
            }
        }
}

    fun sendMsgWithFile(message: String, filePath: String, number: String): Mono<String> {
        val file = java.io.File(filePath)
        val fileBytes = file.readBytes()
        val fileBase64 = java.util.Base64.getEncoder().encodeToString(fileBytes)

        val body = mapOf(
            "number" to number,
            "message" to message,
            "base64" to fileBase64,
            "extension" to "." + file.extension,
            "fileName" to file.name.replace(file.extension, ""),
            "forceSend" to true,
            "verifyContact" to true
        )

        return webClient.post()
            .uri("/send-text-with-file")
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
    }
}