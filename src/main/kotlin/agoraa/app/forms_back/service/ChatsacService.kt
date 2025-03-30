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

    fun sendMessageByContactId(message: String, number: String): Mono<Void> {
        val body = mapOf(
            "message" to message,
            "isWhisper" to false,
            "forceSend" to true,
            "delayInSeconds" to 0,
            "verifyContact" to false
        )

        return webClient.post()
            .uri("/send-text")
            .bodyValue(body)
            .retrieve()
            .onStatus({ status -> status.isError }) { response ->
                response.bodyToMono(String::class.java).flatMap { errorBody ->
                    Mono.error(
                        WebClientResponseException.create(
                            response.statusCode().value(),
                            response.statusCode().toString(),
                            response.headers().asHttpHeaders(),
                            errorBody.toByteArray(),
                            null
                        )
                    )
                }
            }
            .bodyToMono(Void::class.java)
    }
}