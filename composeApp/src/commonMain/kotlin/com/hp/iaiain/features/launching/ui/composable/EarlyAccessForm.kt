package com.hp.iaiain.features.launching.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.hp.iaiain.design.components.PrimaryButton
import com.hp.iaiain.design.components.TextField
import com.hp.iaiain.design.system.CornerRadius
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
            .padding(horizontal = Spacing.xxxl, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF1B3A6B),
                    shape = RoundedCornerShape(CornerRadius.lg)
                )
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Get Early Access",
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite,
                modifier = Modifier.padding(bottom = Spacing.md)
            )

            Text(
                text = "Be the first to access and use our platform once we launch. Join our early access community!",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Spacing.lg)
            )

            // Two-column grid layout for form fields
            // Row 1: Full Name | Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.lg)
            ) {
                TextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    label = "Full Name",
                    placeholder = "Enter your name",
                    error = fullNameError,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                TextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email Address",
                    placeholder = "your email@example.com",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    error = emailError,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            // Row 2: Institution | Role
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.lg)
            ) {
                TextField(
                    value = institution,
                    onValueChange = onInstitutionChange,
                    label = "Institution",
                    placeholder = "Your school, college, or company",
                    error = institutionError,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                TextField(
                    value = role,
                    onValueChange = onRoleChange,
                    label = "Role",
                    placeholder = "Select your role",
                    error = roleError,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            // Referral Code (Full Width)
            TextField(
                value = referralCode,
                onValueChange = onReferralCodeChange,
                label = "Message (Optional)",
                placeholder = "Tell us what excites you about IAIAIN",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg)
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
                    text = "I agree to the Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            PrimaryButton(
                text = "Join the Waitlist",
                onClick = onSubmit,
                enabled = !isSubmitting,
                isLoading = isSubmitting,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.xxxl)
            )
        }
    }
}

