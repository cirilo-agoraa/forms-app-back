package agoraa.app.forms_back.shared.service

import agoraa.app.forms_back.shared.enums.FoldersEnum
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

@Service
class GoogleDriveService {
    private val JSON_FACTORY = GsonFactory.getDefaultInstance()
    private val SCOPES: List<String> = Collections.singletonList(DriveScopes.DRIVE_FILE)
    private val SERVICE_ACCOUNT_CREDENTIALS_PATH: String = "/credentials.json"
    private val APPLICATION_NAME: String = "Agoraa"

    private fun getServiceAccountCredential(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val inputStream: InputStream =
            GoogleDriveService::class.java.getResourceAsStream(SERVICE_ACCOUNT_CREDENTIALS_PATH)
                ?: throw FileNotFoundException("Resource not found: $SERVICE_ACCOUNT_CREDENTIALS_PATH")
        return GoogleCredential.fromStream(inputStream, HTTP_TRANSPORT, JSON_FACTORY)
            .createScoped(SCOPES)
    }

    fun createFile(file: java.io.File, mime: String, folder: agoraa.app.forms_back.shared.enums.FoldersEnum): String {
        /**
         * Attempts to upload a file to Google Drive and returns the file ID.
         *
         * @param file The file to be uploaded.
         * @param fileType The MIME type of the file.
         * @param mime The folder in Google Drive where the file will be uploaded.
         * @return The url of the uploaded file.
         * @throws RuntimeException if an error occurs during file upload.
         */
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val credential = getServiceAccountCredential(HTTP_TRANSPORT)
        val service = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()

        val fileMetadata = File().apply {
            name = file.name
            parents = Collections.singletonList(folder.folderId)
        }

        val mediaContent: FileContent = FileContent(mime, file)
        val createdFile = service.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()

        return "https://drive.google.com/file/d/${createdFile.id}/view"
    }
}