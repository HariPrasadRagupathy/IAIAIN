package com.hp.iaiain.features.launching.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest
import com.hp.iaiain.features.launching.domain.usecase.SubmitEarlyAccessUseCase
import com.hp.iaiain.features.launching.domain.usecase.ValidateEmailUseCase
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingEffect
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingIntent
import com.hp.iaiain.features.launching.presentation.mvi.LaunchingScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LaunchingViewModel(
    private val submitEarlyAccessUseCase: SubmitEarlyAccessUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LaunchingScreenState())
    val state: StateFlow<LaunchingScreenState> = _state

    private val _effect = MutableSharedFlow<LaunchingEffect>()
    val effect: SharedFlow<LaunchingEffect> = _effect

    private var countdownTimer: kotlinx.coroutines.Job? = null

    fun handleIntent(intent: LaunchingIntent) {
        when (intent) {
            is LaunchingIntent.InitializeCountdown -> initializeCountdown()
            is LaunchingIntent.UpdateCountdown -> updateCountdown()
            is LaunchingIntent.UpdateFullName -> updateFullName(intent.name)
            is LaunchingIntent.UpdateEmail -> updateEmail(intent.email)
            is LaunchingIntent.UpdateInstitution -> updateInstitution(intent.institution)
            is LaunchingIntent.UpdateRole -> updateRole(intent.role)
            is LaunchingIntent.UpdateReferralCode -> updateReferralCode(intent.code)
            is LaunchingIntent.UpdateAgreeToTerms -> updateAgreeToTerms(intent.agree)
            is LaunchingIntent.SubmitEarlyAccessRequest -> submitEarlyAccessRequest()
            is LaunchingIntent.ClearError -> clearError()
            is LaunchingIntent.CloseSuccessDialog -> closeSuccessDialog()
            is LaunchingIntent.OpenLink -> openLink(intent.url)
        }
    }

    private fun initializeCountdown() {
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = viewModelScope.launch {
            while (true) {
                updateCountdownTime()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun updateCountdownTime() {
        val currentState = _state.value
        var seconds = currentState.countdownSeconds - 1
        var minutes = currentState.countdownMinutes
        var hours = currentState.countdownHours
        var days = currentState.countdownDays

        when {
            seconds < 0 -> {
                seconds = 59
                minutes -= 1
            }
        }
        when {
            minutes < 0 -> {
                minutes = 59
                hours -= 1
            }
        }
        when {
            hours < 0 -> {
                hours = 23
                days -= 1
            }
        }

        _state.value = currentState.copy(
            countdownDays = maxOf(0, days),
            countdownHours = maxOf(0, hours),
            countdownMinutes = maxOf(0, minutes),
            countdownSeconds = maxOf(0, seconds)
        )
    }

    private fun updateCountdown() {
        handleIntent(LaunchingIntent.UpdateCountdown)
    }

    private fun updateFullName(name: String) {
        _state.value = _state.value.copy(
            fullName = name,
            fullNameError = if (name.isBlank()) "Full name is required" else null
        )
    }

    private fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)

        viewModelScope.launch {
            val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
            val error = when {
                email.isBlank() -> "Email is required"
                !emailRegex.matches(email) -> "Invalid email format"
                else -> null
            }
            _state.value = _state.value.copy(emailError = error)
        }
    }

    private fun updateInstitution(institution: String) {
        _state.value = _state.value.copy(
            institution = institution,
            institutionError = if (institution.isBlank()) "Institution is required" else null
        )
    }

    private fun updateRole(role: String) {
        _state.value = _state.value.copy(
            role = role,
            roleError = if (role.isBlank()) "Role is required" else null
        )
    }

    private fun updateReferralCode(code: String) {
        _state.value = _state.value.copy(referralCode = code)
    }

    private fun updateAgreeToTerms(agree: Boolean) {
        _state.value = _state.value.copy(agreeToTerms = agree)
    }

    private fun submitEarlyAccessRequest() {
        val currentState = _state.value

        if (!currentState.isFormValid) {
            _state.value = currentState.copy(
                errorMessage = "Please fill all required fields correctly"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true)

            val request = EarlyAccessRequest(
                fullName = currentState.fullName,
                email = currentState.email,
                institution = currentState.institution,
                role = currentState.role,
                referralCode = if (currentState.referralCode.isBlank()) null else currentState.referralCode,
                agreeToTerms = currentState.agreeToTerms
            )

            val result = submitEarlyAccessUseCase(request)

            result.onSuccess { response ->
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    showSuccessDialog = true,
                    successMessage = response.message
                )
                _effect.emit(LaunchingEffect.ShowSuccess(response.message))
            }

            result.onFailure { error ->
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = error.message ?: "An error occurred"
                )
                _effect.emit(LaunchingEffect.ShowError(error.message ?: "An error occurred"))
            }
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private fun closeSuccessDialog() {
        _state.value = _state.value.copy(showSuccessDialog = false)
    }

    private fun openLink(url: String) {
        viewModelScope.launch {
            _effect.emit(LaunchingEffect.OpenLink(url))
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownTimer?.cancel()
    }
}

