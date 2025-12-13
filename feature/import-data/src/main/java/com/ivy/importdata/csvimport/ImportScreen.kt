package com.ivy.importdata.csvimport

import com.ivy.wallet.ui.theme.modal.ProgressModal

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ivy.data.backup.ImportResult
import com.ivy.data.backup.drive.DriveFile
import com.ivy.importdata.csvimport.flow.ImportFrom
import com.ivy.importdata.csvimport.flow.ImportProcessing
import com.ivy.importdata.csvimport.flow.ImportResultUI
import com.ivy.importdata.csvimport.flow.instructions.ImportInstructions
import com.ivy.legacy.domain.deprecated.logic.csv.model.ImportType
import com.ivy.navigation.ImportScreen
import com.ivy.onboarding.viewmodel.OnboardingViewModel

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportCSVScreen(screen: ImportScreen) {
    val viewModel: ImportViewModel = viewModel()

    val importStep by viewModel.importStep.observeAsState(ImportStep.IMPORT_FROM)
    val importType by viewModel.importType.observeAsState()
    val importProgressPercent by viewModel.importProgressPercent.observeAsState(0)
    val importResult by viewModel.importResult.observeAsState()

    val signedInAccount by viewModel.signedInAccount
    val driveBackups by viewModel.driveBackups
    val driveRestoreInProgress by viewModel.driveRestoreInProgress

    val onboardingViewModel: OnboardingViewModel = viewModel()
    val context = LocalContext.current
    com.ivy.legacy.utils.onScreenStart {
        viewModel.start(screen)
        viewModel.checkSignedInAccount(context)
    }


    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            if (account != null) {
                viewModel.onGoogleSignInResult(context, account)
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            // Handle error
            timber.log.Timber.e(e)
        }
    }

    UI(
        screen = screen,
        importStep = importStep,
        importType = importType,
        importProgressPercent = importProgressPercent,
        importResult = importResult,

        signedInAccount = signedInAccount,
        driveBackups = driveBackups,
        driveRestoreInProgress = driveRestoreInProgress,

        onChooseImportType = viewModel::setImportType,
        onUploadCSVFile = { viewModel.uploadFile(context) },
        onSkip = {
            viewModel.skip(
                screen = screen,
                onboardingViewModel = onboardingViewModel
            )
        },
        onFinish = {
            viewModel.finish(
                screen = screen,
                onboardingViewModel = onboardingViewModel
            )
        },
        onRestoreFromDrive = { viewModel.restoreFromDrive(context, it) },
        onSignInToDrive = { viewModel.signInToGoogle(context, googleSignInLauncher) }
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: ImportScreen,

    importStep: ImportStep,
    importType: ImportType?,
    importProgressPercent: Int,
    importResult: ImportResult?,

    signedInAccount: GoogleSignInAccount? = null,
    driveBackups: List<DriveFile> = emptyList(),
    driveRestoreInProgress: Boolean = false,

    onChooseImportType: (ImportType) -> Unit = {},
    onUploadCSVFile: () -> Unit = {},
    onSkip: () -> Unit = {},
    onFinish: () -> Unit = {},
    onRestoreFromDrive: (DriveFile) -> Unit = {},
    onSignInToDrive: () -> Unit = {}
) {
    when (importStep) {
        ImportStep.IMPORT_FROM -> {
            ImportFrom(
                hasSkip = screen.launchedFromOnboarding,
                launchedFromOnboarding = screen.launchedFromOnboarding,
                onSkip = onSkip,
                onImportFrom = onChooseImportType,
                signedInAccount = signedInAccount,
                driveBackups = driveBackups,
                onRestoreFromDrive = onRestoreFromDrive,
                onSignInToDrive = onSignInToDrive
            )
        }

        ImportStep.INSTRUCTIONS -> {
            ImportInstructions(
                hasSkip = screen.launchedFromOnboarding,
                importType = importType!!,
                onSkip = onSkip,
                onUploadClick = onUploadCSVFile
            )
        }

        ImportStep.LOADING -> {
            ImportProcessing(
                progressPercent = importProgressPercent
            )
        }

        ImportStep.RESULT -> {
            ImportResultUI(
                result = importResult!!,
                launchedFromOnboarding = screen.launchedFromOnboarding,
            ) {
                onFinish()
            }
        }
    }

    ProgressModal(
        title = "Restoring data", // TODO: Localize
        description = "This may take a while. Please do not close the app.", // TODO: Localize
        visible = driveRestoreInProgress
    )
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    com.ivy.legacy.IvyWalletPreview {
        UI(
            screen = ImportScreen(launchedFromOnboarding = true),
            importStep = ImportStep.IMPORT_FROM,
            importType = null,
            importProgressPercent = 0,
            importResult = null
        )
    }
}
