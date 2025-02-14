package agoraa.app.forms_back.service

import agoraa.app.forms_back.enum.FoldersEnum
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime

@Service
class FileService {
    fun saveFile(file: MultipartFile, folder: String = "OTHERS"): Any {
        // "F:/forms/uploads/${folder}/${file.originalFilename}"
        val filePath = "C:/forms/uploads/${FoldersEnum.valueOf(folder)}/${file.originalFilename}"
        try {
            file.transferTo(File(filePath))
            return mapOf("file" to filePath)
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }
}