package com.hp.iaiain.features.launching.data.repository

import com.hp.iaiain.features.launching.data.source.EarlyAccessRemoteDataSource
import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest
import com.hp.iaiain.features.launching.domain.model.EarlyAccessResponse
import com.hp.iaiain.features.launching.domain.repository.EarlyAccessRepository

class EarlyAccessRepositoryImpl(
    private val remoteDataSource: EarlyAccessRemoteDataSource
) : EarlyAccessRepository {

    override suspend fun submitEarlyAccessRequest(request: EarlyAccessRequest): Result<EarlyAccessResponse> {
        return try {
            val response = remoteDataSource.submitEarlyAccessRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateEmail(email: String): Result<Boolean> {
        return try {
            val isValid = remoteDataSource.validateEmail(email)
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

