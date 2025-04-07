package agoraa.app.forms_back

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class FormsBackApplication

fun main(args: Array<String>) {
    runApplication<FormsBackApplication>(*args)
}
