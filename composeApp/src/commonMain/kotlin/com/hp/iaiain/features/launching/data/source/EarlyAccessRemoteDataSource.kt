package com.hp.iaiain.features.launching.data.source

import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest
import com.hp.iaiain.features.launching.domain.model.EarlyAccessResponse
import kotlin.random.Random

interface EarlyAccessRemoteDataSource {
    suspend fun submitEarlyAccessRequest(request: EarlyAccessRequest): EarlyAccessResponse
    suspend fun validateEmail(email: String): Boolean
}

// Mock implementation for now - will be replaced with actual API calls
class EarlyAccessRemoteDataSourceImpl : EarlyAccessRemoteDataSource {
    override suspend fun submitEarlyAccessRequest(request: EarlyAccessRequest): EarlyAccessResponse {
        // Simulate network call
        val accessCode = Random.nextInt(100000, 999999)
        return EarlyAccessResponse(
            success = true,
            message = "Thank you for your interest! Check your email for next steps.",
            accessCode = "IAIAIN-$accessCode"
        )
    }

    override suspend fun validateEmail(email: String): Boolean {
        // Simulate network call
        return true
    }
}

