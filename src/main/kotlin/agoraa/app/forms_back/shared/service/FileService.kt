package agoraa.app.forms_back.shared.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

@Service
class FileService(private val driveService: GoogleDriveService) {
    fun saveFile(file: MultipartFile, mime: String, folder: String): Any {
        return try {
            val convertedFile = convertMultipartFileToFile(file)
            mapOf("fileUrl" to driveService.createFile(convertedFile, mime, agoraa.app.forms_back.shared.enums.FoldersEnum.valueOf(folder)))
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    private fun convertMultipartFileToFile(multipartFile: MultipartFile): File {
        val tempFile = File.createTempFile("temp", multipartFile.originalFilename)
        FileOutputStream(tempFile).use { outputStream ->
            outputStream.write(multipartFile.bytes)
        }
        return tempFile
    }
}