package com.ivy.importdata.csvimport.flow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.domain.deprecated.logic.csv.model.ImportType
import com.ivy.navigation.CSVScreen
import com.ivy.navigation.navigation
import com.ivy.onboarding.components.OnboardingToolbar
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.components.GradientCutBottom
import com.ivy.wallet.ui.theme.components.IvyIcon
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ivy.data.backup.drive.DriveFile
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.window.DialogProperties

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportFrom(
    hasSkip: Boolean,
    launchedFromOnboarding: Boolean,

    signedInAccount: GoogleSignInAccount? = null,
    driveBackups: List<DriveFile> = emptyList(),

    onSkip: () -> Unit = {},
    onImportFrom: (ImportType) -> Unit = {},
    onRestoreFromDrive: (DriveFile) -> Unit = {},
    onSignInToDrive: () -> Unit = {}
) {
    val importTypes = ImportType.entries.toTypedArray()
    var showBackupDialog by remember { mutableStateOf(false) }

    if (showBackupDialog) {
        BackupSelectionDialog(
            backups = driveBackups,
            onDismiss = { showBackupDialog = false },
            onSelect = {
                showBackupDialog = false
                onRestoreFromDrive(it)
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            val nav = navigation()
            OnboardingToolbar(
                hasSkip = hasSkip,
                onBack = { nav.onBackPressed() },
                onSkip = onSkip
            )
            // onboarding toolbar include paddingBottom 16.dp
        }

        item {
            Spacer(Modifier.height(8.dp))

            // Restore from Drive Section
            if (signedInAccount != null && !launchedFromOnboarding) {
                // User Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            signedInAccount.photoUrl,
                            placeholder = painterResource(R.drawable.ic_profile),
                            error = painterResource(R.drawable.ic_profile),
                        ),
                        contentDescription = "Google Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(UI.colors.primary)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = signedInAccount.displayName ?: "",
                            style = UI.typo.b1.style(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = signedInAccount.email ?: "",
                            style = UI.typo.b2.style(color = Color.Gray)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    onClick = { showBackupDialog = true }
                ) {
                    Text(
                        text = "Restore from Google Drive", // TODO: Localize
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                    )
                }
            } else if (!launchedFromOnboarding) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    onClick = onSignInToDrive
                ) {
                    Text(
                        text = "Restore from Google Drive", // TODO: Localize
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            val nav = navigation()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                onClick = {
                    nav.navigateTo(CSVScreen(launchedFromOnboarding))
                }
            ) {
                Text(
                    text = stringResource(id = R.string.manual_csv_import),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = stringResource(R.string.import_from),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(24.dp))
        }

        items(importTypes) {
            ImportOption(
                importType = it,
                onImportFrom = onImportFrom
            )
        }

        item {
            // last spacer
            Spacer(Modifier.height(96.dp))
        }
    }

    GradientCutBottom(
        height = 96.dp
    )
}

@Composable
private fun ImportOption(
    importType: ImportType,
    onImportFrom: (ImportType) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(UI.shapes.r3)
                .background(UI.colors.medium, UI.shapes.r3)
                .clickable {
                    onImportFrom(importType)
                }
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            IvyIcon(
                modifier = Modifier.size(32.dp),
                icon = importType.logo(),
                tint = Color.Unspecified
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, end = 32.dp),
                text = importType.listName(),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}


@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    com.ivy.legacy.IvyWalletPreview {
        ImportFrom(
            hasSkip = true,
            launchedFromOnboarding = false,
        )
    }
}

@Composable
private fun BackupSelectionDialog(
    backups: List<DriveFile>,
    onDismiss: () -> Unit,
    onSelect: (DriveFile) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Backup",
                    style = UI.typo.h2.style(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (backups.isEmpty()) {
                    Text(
                        text = "No backups found.",
                        style = UI.typo.b2.style(color = Color.Gray),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(300.dp) // Limit height
                    ) {
                        items(backups) { file ->
                            BackupItem(
                                file = file,
                                onClick = { onSelect(file) })
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun BackupItem(
    file: DriveFile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyIcon(
            icon = R.drawable.ic_vue_brands_drive,
            tint = UI.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = file.name,
                style = UI.typo.b1.style(fontWeight = FontWeight.Bold)
            )
            file.createdTime?.let {
                Text(
                    text = it,
                    style = UI.typo.b2.style(color = Color.Gray)
                )
            }
        }
    }
}
