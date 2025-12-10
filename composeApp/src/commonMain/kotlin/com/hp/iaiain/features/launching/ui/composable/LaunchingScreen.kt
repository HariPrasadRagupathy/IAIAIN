package com.hp.iaiain.features.launching.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import com.hp.iaiain.design.system.BackgroundDark
import com.hp.iaiain.design.system.Spacing
import com.hp.iaiain.design.system.TextGray
import com.hp.iaiain.design.system.TextWhite
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingIntent
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingScreenState

@Composable
fun LaunchingScreen(
    state: LaunchingScreenState,
    onIntent: (LaunchingIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section
        LaunchingHeaderSection()

        // Features Section
        FeaturesSection()

        // Countdown Section
        CountdownSection(
            days = state.countdownDays,
            hours = state.countdownHours,
            minutes = state.countdownMinutes,
            seconds = state.countdownSeconds
        )

        // Early Access Form Section
        EarlyAccessSection(
            fullName = state.fullName,
            onFullNameChange = { onIntent(LaunchingIntent.UpdateFullName(it)) },
            email = state.email,
            onEmailChange = { onIntent(LaunchingIntent.UpdateEmail(it)) },
            institution = state.institution,
            onInstitutionChange = { onIntent(LaunchingIntent.UpdateInstitution(it)) },
            role = state.role,
            onRoleChange = { onIntent(LaunchingIntent.UpdateRole(it)) },
            referralCode = state.referralCode,
            onReferralCodeChange = { onIntent(LaunchingIntent.UpdateReferralCode(it)) },
            agreeToTerms = state.agreeToTerms,
            onAgreeToTermsChange = { onIntent(LaunchingIntent.UpdateAgreeToTerms(it)) },
            isSubmitting = state.isSubmitting,
            onSubmit = { onIntent(LaunchingIntent.SubmitEarlyAccessRequest) },
            fullNameError = state.fullNameError,
            emailError = state.emailError,
            institutionError = state.institutionError,
            roleError = state.roleError
        )

        // Footer Section
        FooterSection(
            onSocialLinkClick = { url -> onIntent(LaunchingIntent.OpenLink(url)) }
        )

        // Error Dialog
        if (state.errorMessage != null) {
            ErrorDialog(
                message = state.errorMessage,
                onDismiss = { onIntent(LaunchingIntent.ClearError) }
            )
        }

        // Success Dialog
        if (state.showSuccessDialog) {
            SuccessDialog(
                message = state.successMessage,
                onDismiss = { onIntent(LaunchingIntent.CloseSuccessDialog) }
            )
        }
    }
}

@Composable
fun FooterSection(
    onSocialLinkClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Follow us for updates",
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            IconButton(onClick = { onSocialLinkClick("https://facebook.com") }) {
                Text("f", color = TextWhite)
            }
            IconButton(onClick = { onSocialLinkClick("https://twitter.com") }) {
                Text("𝕏", color = TextWhite)
            }
            IconButton(onClick = { onSocialLinkClick("https://linkedin.com") }) {
                Text("in", color = TextWhite)
            }
            IconButton(onClick = { onSocialLinkClick("https://instagram.com") }) {
                Text("◉", color = TextWhite)
            }
        }
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success!") },
        text = { Text(message) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Continue")
            }
        }
    )
}

