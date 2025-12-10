package com.hp.iaiain.features.launching.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.hp.iaiain.design.components.PrimaryButton
import com.hp.iaiain.design.components.TextField
import com.hp.iaiain.design.system.BackgroundDark
import com.hp.iaiain.design.system.Spacing
import com.hp.iaiain.design.system.TextGray
import com.hp.iaiain.design.system.TextWhite

@Composable
fun EarlyAccessSection(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    institution: String,
    onInstitutionChange: (String) -> Unit,
    role: String,
    onRoleChange: (String) -> Unit,
    referralCode: String,
    onReferralCodeChange: (String) -> Unit,
    agreeToTerms: Boolean,
    onAgreeToTermsChange: (Boolean) -> Unit,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    fullNameError: String?,
    emailError: String?,
    institutionError: String?,
    roleError: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Get Early Access",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )

        Text(
            text = "Be the first to access and use our platform once we launch. Join our early access community!",
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )

        // Form Fields
        TextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = "Full Name *",
            placeholder = "Enter your full name",
            error = fullNameError,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address *",
            placeholder = "Enter your email",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            error = emailError,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        TextField(
            value = institution,
            onValueChange = onInstitutionChange,
            label = "Institution/Company *",
            placeholder = "Enter your institution",
            error = institutionError,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        TextField(
            value = role,
            onValueChange = onRoleChange,
            label = "Your Role *",
            placeholder = "Select your role",
            error = roleError,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        TextField(
            value = referralCode,
            onValueChange = onReferralCodeChange,
            label = "Referral Code (Optional)",
            placeholder = "Do you have a referral code?",
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        // Terms Checkbox
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Checkbox(
                checked = agreeToTerms,
                onCheckedChange = onAgreeToTermsChange,
                modifier = Modifier.padding(end = Spacing.sm)
            )
            Text(
                text = "I agree to the Terms of Service and Privacy Policy *",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }

        PrimaryButton(
            text = "Get Early Access",
            onClick = onSubmit,
            enabled = !isSubmitting,
            isLoading = isSubmitting,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )
    }
}

