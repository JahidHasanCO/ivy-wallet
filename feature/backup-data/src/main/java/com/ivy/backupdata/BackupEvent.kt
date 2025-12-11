package com.ivy.backupdata

import com.ivy.domain.RootScreen

sealed interface BackupEvent {
    data class BackupData(val rootScreen: RootScreen) : BackupEvent
}
