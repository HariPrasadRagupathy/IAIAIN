package com.hp.iaiain.di

import com.hp.iaiain.features.launching.data.repository.EarlyAccessRepositoryImpl
import com.hp.iaiain.features.launching.data.source.EarlyAccessRemoteDataSourceImpl
import com.hp.iaiain.features.launching.domain.repository.EarlyAccessRepository
import com.hp.iaiain.features.launching.domain.usecase.SubmitEarlyAccessUseCase
import com.hp.iaiain.features.launching.domain.usecase.ValidateEmailUseCase
import com.hp.iaiain.features.launching.presentation.viewmodel.LaunchingViewModel

object ServiceLocator {
    // Data Sources
    private val earlyAccessRemoteDataSource: EarlyAccessRemoteDataSourceImpl by lazy {
        EarlyAccessRemoteDataSourceImpl()
    }

    // Repositories
    private val earlyAccessRepository: EarlyAccessRepository by lazy {
        EarlyAccessRepositoryImpl(earlyAccessRemoteDataSource)
    }

    // Use Cases
    private val submitEarlyAccessUseCase: SubmitEarlyAccessUseCase by lazy {
        SubmitEarlyAccessUseCase(earlyAccessRepository)
    }

    private val validateEmailUseCase: ValidateEmailUseCase by lazy {
        ValidateEmailUseCase(earlyAccessRepository)
    }

    // ViewModels
    fun createLaunchingViewModel(): LaunchingViewModel {
        return LaunchingViewModel(
            submitEarlyAccessUseCase = submitEarlyAccessUseCase,
            validateEmailUseCase = validateEmailUseCase
        )
    }
}

