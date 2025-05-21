package agoraa.app.forms_back.survery.controller

import agoraa.app.forms_back.survery.dto.SurveyRequest
import agoraa.app.forms_back.survery.service.SurveyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestBody
import agoraa.app.forms_back.survery.dto.SurveyAnswerRequest

@RestController
@RequestMapping("/api/surveys")
class SurveyController(private val service: SurveyService) {

    @GetMapping
    fun getAll() = ResponseEntity.ok(service.findAllWithQuestions())

    @GetMapping("/edit/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(service.findSurveyById(id))

    @PostMapping("/response")
    fun saveResponses(@RequestBody request: SurveyAnswerRequest): ResponseEntity<Void> {
        service.saveSurveyResponses(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
    
    @GetMapping("/user/{id}")
    fun getByUser(@PathVariable id: Long) = ResponseEntity.ok(service.findAllByLoggedUser(id))

    @PostMapping
    fun create(@RequestBody request: SurveyRequest) =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: SurveyRequest) =
        ResponseEntity.ok(service.update(id, request))
}