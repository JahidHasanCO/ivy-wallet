package com.ivy.backupdata

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.backupdata.flow.BackupTo
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.rootScreen
import com.ivy.navigation.BackupScreen
import com.ivy.onboarding.viewmodel.OnboardingViewModel
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.modal.ProgressModal
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

@OptIn(ExperimentalStdlibApi::class, ExperimentalFoundationApi::class)
@Composable
fun BoxWithConstraintsScope.BackupStepScreen(
    screen: BackupScreen,
) {
    val viewModel: BackupViewModel = viewModel()
    val backupStep by viewModel.backupStep.observeAsState(BackupStep.BACKUP_TO)
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val rootScreen = rootScreen()
    val uiState = viewModel.uiState()
    val signedInAccount by viewModel.signedInAccountState

    val context = LocalContext.current

    com.ivy.legacy.utils.onScreenStart {
        viewModel.checkSignedInAccount(context)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        task.addOnSuccessListener { account ->
            viewModel.onGoogleSignInResult(account)
            viewModel.backupToGoogleDrive(context)
        }
    }
    UI(
        screen = screen,
        backupStep = backupStep,
        progressState = uiState.progressState,
        signedInAccount = signedInAccount,
        onBackupData = {
            viewModel.onEvent(BackupEvent.BackupData(rootScreen))
        },
        onBackupToDrive = {
            val account = signedInAccount
            if (account != null) {
                viewModel.backupToGoogleDrive(context)
            } else {
                viewModel.signInToGoogle(context, googleSignInLauncher)
            }
        })
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: BackupScreen,
    backupStep: BackupStep,
    onSkip: () -> Unit = {},
    progressState: Boolean = false,
    onBackupData: () -> Unit = {},
    onBackupToDrive: () -> Unit = {},
    signedInAccount: GoogleSignInAccount? = null,
) {
    when (backupStep) {
        BackupStep.BACKUP_TO -> {
            BackupTo(
                hasSkip = screen.launchedFromOnboarding,
                launchedFromOnboarding = screen.launchedFromOnboarding,
                onSkip = onSkip,
                signedInAccount = signedInAccount,
                onBackupData = onBackupData,
                onBackupNow = onBackupToDrive
            )
        }
    }

    ProgressModal(
        title = stringResource(R.string.exporting_data),
        description = stringResource(R.string.exporting_data_description),
        visible = progressState
    )
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            screen = BackupScreen(launchedFromOnboarding = true),
            backupStep = BackupStep.BACKUP_TO
        )
    }
}
