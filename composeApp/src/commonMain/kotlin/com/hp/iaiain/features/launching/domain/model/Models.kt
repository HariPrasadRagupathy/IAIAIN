package com.hp.iaiain.features.launching.domain.model

data class LaunchingState(
    val daysRemaining: Int = 0,
    val hoursRemaining: Int = 0,
    val minutesRemaining: Int = 0,
    val secondsRemaining: Int = 0,
    val isCountdownActive: Boolean = true
)

data class EarlyAccessRequest(
    val fullName: String = "",
    val email: String = "",
    val institution: String = "",
    val role: String = "",
    val referralCode: String? = null,
    val agreeToTerms: Boolean = false
)

data class EarlyAccessResponse(
    val success: Boolean,
    val message: String,
    val accessCode: String? = null
)

sealed class LaunchingEffect {
    data class ShowSuccess(val message: String) : LaunchingEffect()
    data class ShowError(val message: String) : LaunchingEffect()
    data class OpenLink(val url: String) : LaunchingEffect()
}

