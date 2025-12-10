package com.hp.iaiain.features.launching.domain.usecase

import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest
import com.hp.iaiain.features.launching.domain.model.EarlyAccessResponse
import com.hp.iaiain.features.launching.domain.repository.EarlyAccessRepository

class SubmitEarlyAccessUseCase(
    private val repository: EarlyAccessRepository
) {
    suspend operator fun invoke(request: EarlyAccessRequest): Result<EarlyAccessResponse> {
        // Validate input
        if (request.fullName.isBlank()) {
            return Result.failure(IllegalArgumentException("Full name is required"))
        }
        if (request.email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email is required"))
        }
        if (!request.agreeToTerms) {
            return Result.failure(IllegalArgumentException("You must agree to terms"))
        }

        // Validate email format
        if (!isValidEmail(request.email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        return repository.submitEarlyAccessRequest(request)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        return emailRegex.matches(email)
    }
}

class ValidateEmailUseCase(
    private val repository: EarlyAccessRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        if (email.isBlank()) {
            return Result.success(false)
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        if (!emailRegex.matches(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        return repository.validateEmail(email)
    }
}

