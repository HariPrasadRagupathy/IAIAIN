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
import com.hp.iaiain.util.getCurrentSystemDateTime

class LaunchingViewModel(
    private val submitEarlyAccessUseCase: SubmitEarlyAccessUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LaunchingScreenState())
    val state: StateFlow<LaunchingScreenState> = _state

    private val _effect = MutableSharedFlow<LaunchingEffect>()
    val effect: SharedFlow<LaunchingEffect> = _effect

    private var countdownTimer: kotlinx.coroutines.Job? = null

    // Target launch date: March 24, 2026 at 10:00 AM
    // This is calculated as epoch milliseconds for the target date
    private val targetLaunchDate = TargetDate(year = 2026, month = 3, day = 24, hour = 10, minute = 0, second = 0)

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
        val remainingTime = calculateTimeToTarget()
        _state.value = _state.value.copy(
            countdownDays = remainingTime.days,
            countdownHours = remainingTime.hours,
            countdownMinutes = remainingTime.minutes,
            countdownSeconds = remainingTime.seconds
        )
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                updateCountdownTime()
            }
        }
    }

    private fun updateCountdownTime() {
        val currentState = _state.value
        var seconds = currentState.countdownSeconds - 1
        var minutes = currentState.countdownMinutes
        var hours = currentState.countdownHours
        var days = currentState.countdownDays

        // Handle negative seconds
        if (seconds < 0) {
            seconds = 59
            minutes -= 1
        }

        // Handle negative minutes
        if (minutes < 0) {
            minutes = 59
            hours -= 1
        }

        // Handle negative hours
        if (hours < 0) {
            hours = 23
            days -= 1
        }

        // Stop countdown at 0
        if (days < 0) {
            _state.value = currentState.copy(
                countdownDays = 0,
                countdownHours = 0,
                countdownMinutes = 0,
                countdownSeconds = 0
            )
            countdownTimer?.cancel()
            return
        }

        _state.value = currentState.copy(
            countdownDays = days,
            countdownHours = hours,
            countdownMinutes = minutes,
            countdownSeconds = seconds
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

    private fun calculateTimeToTarget(): CountdownTime {
        val now = getCurrentDateTime()
        val target = targetLaunchDate

        // Calculate total seconds until target
        var totalSeconds = 0L

        // First, calculate days from now until target (same time of day)
        val daysRemaining = daysUntilDate(
            now.year, now.month, now.day,
            target.year, target.month, target.day
        )

        // Add days to total seconds
        totalSeconds += daysRemaining * 86400L // 86400 seconds in a day

        // Now handle the time portion
        val currentTimeInSeconds = now.hour * 3600 + now.minute * 60 + now.second
        val targetTimeInSeconds = target.hour * 3600 + target.minute * 60 + target.second

        val timeRemainingSeconds = targetTimeInSeconds - currentTimeInSeconds

        // If time has already passed today, adjust
        if (timeRemainingSeconds < 0) {
            totalSeconds -= 86400L // subtract one day
            totalSeconds += (86400L + timeRemainingSeconds) // add remaining seconds from today
        } else {
            totalSeconds += timeRemainingSeconds
        }

        // Stop if target time has passed
        if (totalSeconds <= 0) {
            return CountdownTime(days = 0, hours = 0, minutes = 0, seconds = 0)
        }

        // Convert total seconds to days, hours, minutes, seconds
        val days = (totalSeconds / 86400L).toInt()
        val hours = ((totalSeconds % 86400L) / 3600L).toInt()
        val minutes = ((totalSeconds % 3600L) / 60L).toInt()
        val seconds = (totalSeconds % 60L).toInt()

        return CountdownTime(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )
    }

    private fun daysUntilDate(
        fromYear: Int, fromMonth: Int, fromDay: Int,
        toYear: Int, toMonth: Int, toDay: Int
    ): Int {
        var days = 0
        var year = fromYear
        var month = fromMonth
        var day = fromDay

        // If dates are same, return 0
        if (year == toYear && month == toMonth && day == toDay) {
            return 0
        }

        // If target is before current date, return negative
        if (year > toYear || (year == toYear && month > toMonth) ||
            (year == toYear && month == toMonth && day > toDay)) {
            return -1
        }

        // Count days until target
        while (year < toYear || month < toMonth || day < toDay) {
            val daysInCurrentMonth = getDaysInMonth(month, year)

            if (day == toDay && year == toYear && month == toMonth) {
                break
            }

            if (day < daysInCurrentMonth) {
                day++
                days++
            } else {
                day = 1
                month++
                if (month > 12) {
                    month = 1
                    year++
                }
            }
        }

        return days
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun getCurrentDateTime(): CurrentDateTime {
        // Get actual current date and time from system using platform-specific implementation
        val systemDateTime = getCurrentSystemDateTime()

        return CurrentDateTime(
            year = systemDateTime.year,
            month = systemDateTime.month,
            day = systemDateTime.day,
            hour = systemDateTime.hour,
            minute = systemDateTime.minute,
            second = systemDateTime.second
        )
    }
}

// Data classes for countdown calculation
data class TargetDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int
)

data class CurrentDateTime(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int
)

data class CountdownTime(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)

