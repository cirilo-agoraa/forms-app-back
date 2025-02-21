package agoraa.app.forms_back.service

import agoraa.app.forms_back.enum.FoldersEnum
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

@Service
class FileService(private val driveService: GoogleDriveService) {
    fun saveFile(file: MultipartFile, mime: String, folder: String): Any {
        return try {
            val convertedFile = convertMultipartFileToFile(file)
            mapOf("fileUrl" to driveService.createFile(convertedFile, mime, FoldersEnum.valueOf(folder)))
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