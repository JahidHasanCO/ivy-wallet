package com.ivy.backupdata

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ivy.base.legacy.SharedPrefs
import com.ivy.data.backup.BackupDataUseCase
import com.ivy.data.backup.drive.GoogleDriveBackupManager
import com.ivy.domain.RootScreen
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.utils.asLiveData
import com.ivy.legacy.utils.getISOFormattedDateTime
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.legacy.utils.uiThread
import com.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State



@HiltViewModel
class BackupViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val sharedPrefs: SharedPrefs,
    private val backupDataUseCase: BackupDataUseCase,
) : ComposeViewModel<BackupState, BackupEvent>() {

    private val _backupStep = MutableLiveData<BackupStep>()
    val backupStep = _backupStep.asLiveData()

    private val progressState = mutableStateOf(false)
    private val driveProgressState = mutableStateOf(false)
    private val googleBackupResult = mutableStateOf<Result<String>?>(null)
    private val signedInAccount = mutableStateOf<GoogleSignInAccount?>(null)
    val signedInAccountState: State<GoogleSignInAccount?> get() = signedInAccount

    @Composable
    override fun uiState(): BackupState {
        return BackupState(
            progressState = getProgressState(),
            driveProgressState = getDriveProgressState()
        )
    }

    override fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.BackupData -> exportToZip(event.rootScreen)
            is BackupEvent.BackupToDrive -> backupToGoogleDrive(context = event.context)
        }
    }

    @Composable
    private fun getProgressState(): Boolean {
        return progressState.value
    }

    @Composable
    private fun getDriveProgressState(): Boolean {
        return driveProgressState.value
    }

    private fun exportToZip(rootScreen: RootScreen) {
        ivyContext.createNewFile(
            "IvyWalletBackup_${
                timeNowUTC().getISOFormattedDateTime()
            }.zip"
        ) { fileUri ->
            viewModelScope.launch(Dispatchers.IO) {
                progressState.value = true
                backupDataUseCase.exportToFile(zipFileUri = fileUri)
                progressState.value = false

                sharedPrefs.putBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, true)
                ivyContext.dataBackupCompleted = true

                uiThread {
                    rootScreen.shareZipFile(
                        fileUri = fileUri
                    )
                }
            }
        }
    }

    fun checkSignedInAccount(context: Context) {
        signedInAccount.value = GoogleSignIn.getLastSignedInAccount(context)
    }

    fun signInToGoogle(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()

        val client = GoogleSignIn.getClient(context, gso)
        launcher.launch(client.signInIntent)
    }

    fun onGoogleSignInResult(account: GoogleSignInAccount) {
        signedInAccount.value = account
    }

    fun backupToGoogleDrive(context: Context) {
        val account = signedInAccount.value ?: return
        driveProgressState.value = true

        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_FILE)
        ).apply { selectedAccount = account.account }

        val driveService = Drive.Builder(
            com.google.api.client.http.javanet.NetHttpTransport(),
            com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("IvyWallet").build()

        val driveBackupManager = GoogleDriveBackupManager(context, driveService)

        viewModelScope.launch(Dispatchers.IO) {
            googleBackupResult.value = backupDataUseCase.backupToGoogleDrive(driveBackupManager)
            driveProgressState.value = false
        }
    }


}
