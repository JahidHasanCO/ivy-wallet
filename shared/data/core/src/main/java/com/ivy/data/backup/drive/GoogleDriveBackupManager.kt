package com.ivy.data.backup.drive

import android.content.Context
import android.net.Uri
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.io.File as LocalFile
import javax.inject.Inject
import kotlin.io.outputStream

class GoogleDriveBackupManager @Inject constructor(
    private val context: Context, private val driveService: Drive
) {

    /**
     * Upload ZIP backup to Google Drive
     */
    suspend fun uploadBackupToDrive(zipUri: Uri): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val localFile = LocalFile(zipUri.path!!)
                val fileMetadata = File().apply {
                    name = localFile.name
                    mimeType = "application/zip"
                }

                val mediaContent = FileContent("application/zip", localFile)

                val uploadedFile =
                    driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id").execute()

                Result.success(uploadedFile.id)

            } catch (e: UserRecoverableAuthIOException) {
                // you must catch this in ViewModel and trigger permission request
                Result.failure(e)
            } catch (e: Exception) {
                Timber.e(e)
                Result.failure(e)
            }
        }

    suspend fun downloadBackupFromDrive(fileId: String): Result<LocalFile> =
        withContext(Dispatchers.IO) {
            try {
                val outputFile = LocalFile(
                    context.cacheDir,
                    "restore_${System.currentTimeMillis()}.zip"
                )
                driveService.files().get(fileId)
                    .executeMediaAndDownloadTo(outputFile.outputStream())

                Result.success(outputFile)

            } catch (e: UserRecoverableAuthIOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    suspend fun listBackups(): Result<List<DriveFile>> =
        withContext(Dispatchers.IO) {
            try {
                val fileList = driveService.files().list()
                    .setQ("mimeType = 'application/zip' and (name contains 'IvyWalletBackup' or name contains 'backup_') and explicitlyTrashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, createdTime)")
                    .setOrderBy("createdTime desc").execute()

                val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm a")
                    .withZone(ZoneId.systemDefault())

                val files = fileList.files.map {
                    val createdTimeFormatted = if (it.createdTime != null) {
                        val instant = Instant.parse(it.createdTime.toStringRfc3339())
                        formatter.format(instant)
                    } else {
                        null
                    }
                    DriveFile(
                        id = it.id,
                        name = it.name,
                        createdTime = createdTimeFormatted
                    )
                }

                Result.success(files)
            } catch (e: Exception) {
                Timber.e(e)
                Result.failure(e)
            }
        }
}

data class DriveFile(
    val id: String, val name: String, val createdTime: String?
)
