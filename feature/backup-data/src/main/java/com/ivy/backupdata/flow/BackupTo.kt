package com.ivy.backupdata.flow


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.navigation.navigation
import com.ivy.onboarding.components.OnboardingToolbar
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.components.GradientCutBottom

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.BackupTo(
    hasSkip: Boolean,
    launchedFromOnboarding: Boolean,
    signedInAccount: GoogleSignInAccount? = null,
    lastBackupDate: String? = null,
    onSkip: () -> Unit = {},
    onBackupData: () -> Unit = {},
    onBackupNow: () -> Unit = {} // replaced onBackupToDrive
) {
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
        }

        item {
            Spacer(Modifier.height(8.dp))
            val nav = navigation()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp), onClick = {
                    onBackupData();
                }) {
                Text(
                    text = stringResource(id = R.string.manual_backup),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = stringResource(R.string.backup_to),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(24.dp))
        }

        item {
            // Show Google account info if logged in
            signedInAccount?.let { account ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            account.photoUrl,
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
                            text = account.displayName ?: "",
                            style = UI.typo.b1.style(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = account.email ?: "",
                            style = UI.typo.b2.style(color = Color.Gray)
                        )
                        lastBackupDate?.let { date ->
                            Text(
                                text = "Last backup: $date",
                                style = UI.typo.nB2.style(color = Color.Gray)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp), onClick = onBackupNow
            ) {
                Text(
                    text = stringResource(id = R.string.backup_now),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            Spacer(Modifier.height(96.dp))
        }
    }

    GradientCutBottom(
        height = 96.dp
    )
}


@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    com.ivy.legacy.IvyWalletPreview {
        BackupTo(
            hasSkip = true,
            launchedFromOnboarding = false,
        )
    }
}
