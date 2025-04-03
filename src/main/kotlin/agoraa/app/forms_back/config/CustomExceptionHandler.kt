package agoraa.app.forms_back.config

import agoraa.app.forms_back.shared.exception.NotAllowedException
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(agoraa.app.forms_back.shared.exception.ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: agoraa.app.forms_back.shared.exception.ResourceNotFoundException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Resource Not Found: ${ex.message}", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Bad Request: ${ex.message}", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(agoraa.app.forms_back.shared.exception.NotAllowedException::class)
    fun handleNotAllowedException(ex: agoraa.app.forms_back.shared.exception.NotAllowedException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Forbidden: ${ex.message}", HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Internal Server Error: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}