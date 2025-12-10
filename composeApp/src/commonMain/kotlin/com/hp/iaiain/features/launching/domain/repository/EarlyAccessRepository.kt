package com.hp.iaiain.features.launching.domain.repository

import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest
import com.hp.iaiain.features.launching.domain.model.EarlyAccessResponse

interface EarlyAccessRepository {
    suspend fun submitEarlyAccessRequest(request: EarlyAccessRequest): Result<EarlyAccessResponse>
    suspend fun validateEmail(email: String): Result<Boolean>
}

