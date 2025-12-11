package com.ivy.backupdata

import android.content.Context
import com.ivy.domain.RootScreen

sealed interface BackupEvent {
    data class BackupData(val rootScreen: RootScreen) : BackupEvent
    data class BackupToDrive(val context: Context) : BackupEvent
}
