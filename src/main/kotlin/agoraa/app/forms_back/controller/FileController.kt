package agoraa.app.forms_back.controller

import agoraa.app.forms_back.service.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam file: MultipartFile,
        @RequestParam folder: String,
        @RequestParam mime: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(fileService.saveFile(file, mime, folder))
    }
}