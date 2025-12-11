package com.ivy.backupdata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.SharedPrefs
import com.ivy.data.backup.BackupDataUseCase
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


@HiltViewModel
class BackupViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val sharedPrefs: SharedPrefs,
    private val backupDataUseCase: BackupDataUseCase,
) : ComposeViewModel<BackupState, BackupEvent>() {

    private val _backupStep = MutableLiveData<BackupStep>()
    val backupStep = _backupStep.asLiveData()

    private val progressState = mutableStateOf(false)

    @Composable
    override fun uiState(): BackupState {
        return BackupState(
            progressState = getProgressState()
        )
    }

    override fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.BackupData -> exportToZip(event.rootScreen)
        }
    }

    @Composable
    private fun getProgressState(): Boolean {
        return progressState.value
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
}
