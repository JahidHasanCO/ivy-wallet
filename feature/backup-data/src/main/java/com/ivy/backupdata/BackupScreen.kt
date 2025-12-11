package com.ivy.backupdata

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.backupdata.flow.BackupTo
import com.ivy.legacy.IvyWalletPreview
import com.ivy.navigation.BackupScreen
import com.ivy.navigation.ImportScreen
import com.ivy.onboarding.viewmodel.OnboardingViewModel


@OptIn(ExperimentalStdlibApi::class)
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.BackupStepScreen(screen: BackupScreen) {
    val viewModel: BackupViewModel = viewModel()
    val backupStep by viewModel.backupStep.observeAsState(BackupStep.BACKUP_TO)
    val onboardingViewModel: OnboardingViewModel = viewModel()

    com.ivy.legacy.utils.onScreenStart {
//        viewModel.start(screen)
    }
    val context = LocalContext.current

    UI(
        screen = screen,
        backupStep = backupStep,
    )
}


@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: BackupScreen,
    backupStep: BackupStep,
    onSkip: () -> Unit = {},
) {
    when (backupStep) {
        BackupStep.BACKUP_TO -> {
            BackupTo(
                hasSkip = screen.launchedFromOnboarding,
                launchedFromOnboarding = screen.launchedFromOnboarding,
                onSkip = onSkip,
            )
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            screen = BackupScreen(launchedFromOnboarding = true),
            backupStep = BackupStep.BACKUP_TO,
        )
    }
}
