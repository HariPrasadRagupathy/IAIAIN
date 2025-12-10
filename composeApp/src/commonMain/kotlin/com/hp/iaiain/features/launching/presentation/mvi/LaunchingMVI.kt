package com.hp.iaiain.features.launching.presentation.mvi

import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest

// State
data class LaunchingScreenState(
    val countdownDays: Int = 30,
    val countdownHours: Int = 0,
    val countdownMinutes: Int = 0,
    val countdownSeconds: Int = 0,

    // Form state
    val fullName: String = "",
    val email: String = "",
    val institution: String = "",
    val role: String = "",
    val referralCode: String = "",
    val agreeToTerms: Boolean = false,

    // UI state
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val successMessage: String = "",
    val errorMessage: String? = null,

    // Validation
    val fullNameError: String? = null,
    val emailError: String? = null,
    val institutionError: String? = null,
    val roleError: String? = null
) {
    val isFormValid: Boolean
        get() = fullName.isNotBlank() &&
                email.isNotBlank() &&
                institution.isNotBlank() &&
                role.isNotBlank() &&
                agreeToTerms &&
                fullNameError == null &&
                emailError == null
}

// Intent
sealed class LaunchingIntent {
    // Lifecycle
    data object InitializeCountdown : LaunchingIntent()
    data object UpdateCountdown : LaunchingIntent()

    // Form Inputs
    data class UpdateFullName(val name: String) : LaunchingIntent()
    data class UpdateEmail(val email: String) : LaunchingIntent()
    data class UpdateInstitution(val institution: String) : LaunchingIntent()
    data class UpdateRole(val role: String) : LaunchingIntent()
    data class UpdateReferralCode(val code: String) : LaunchingIntent()
    data class UpdateAgreeToTerms(val agree: Boolean) : LaunchingIntent()

    // Actions
    data object SubmitEarlyAccessRequest : LaunchingIntent()
    data object ClearError : LaunchingIntent()
    data object CloseSuccessDialog : LaunchingIntent()

    // Navigation
    data class OpenLink(val url: String) : LaunchingIntent()
}

// Effect
sealed class LaunchingEffect {
    data class ShowSuccess(val message: String) : LaunchingEffect()
    data class ShowError(val message: String) : LaunchingEffect()
    data class OpenLink(val url: String) : LaunchingEffect()
    data object NavigateToNextScreen : LaunchingEffect()
}
