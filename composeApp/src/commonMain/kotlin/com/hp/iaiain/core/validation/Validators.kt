package com.hp.iaiain.core.validation

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}

object EmailValidator {
    fun validate(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email is required")
            !isValidEmail(email) -> ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        return emailRegex.matches(email)
    }
}

object NameValidator {
    fun validate(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("Name is required")
            name.length < 2 -> ValidationResult.Invalid("Name must be at least 2 characters")
            name.length > 100 -> ValidationResult.Invalid("Name must not exceed 100 characters")
            else -> ValidationResult.Valid
        }
    }
}

object FieldValidator {
    fun validate(field: String, fieldName: String): ValidationResult {
        return when {
            field.isBlank() -> ValidationResult.Invalid("$fieldName is required")
            else -> ValidationResult.Valid
        }
    }
}

