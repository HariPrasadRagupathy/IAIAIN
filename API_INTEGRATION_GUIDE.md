# API Integration Guide - IAIAIN

## Overview

This guide explains how to integrate real API calls into the IAIAIN application using the established architecture.

## Current State

The application currently uses a **mock DataSource** for development and testing. The architecture is designed to make integration seamless.

### Mock Flow

```
UI → ViewModel → UseCase → Repository → MockDataSource (returns canned responses)
```

### Real API Flow

```
UI → ViewModel → UseCase → Repository → RealDataSource → HTTP Client → Backend API
```

## Integration Steps

### Step 1: Add HTTP Client Dependency

Edit `gradle/libs.versions.toml`:

```toml
[versions]
# ... existing versions ...
ktor-client = "3.3.3"  # or latest stable

[libraries]
# ... existing libraries ...
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor-client" }
ktor-client-contentNegotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor-client" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor-client" }
ktor-client-android = { module = "io.ktor:ktor-client-android", version.ref = "ktor-client" }
ktor-client-ios = { module = "io.ktor:ktor-client-ios", version.ref = "ktor-client" }
ktor-client-js = { module = "io.ktor:ktor-client-js", version.ref = "ktor-client" }

# For serialization
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.6.0" }
```

Edit `composeApp/build.gradle.kts`:

```kotlin
dependencies {
    commonMain.dependencies {
        // ... existing dependencies ...
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.contentNegotiation)
        implementation(libs.ktor.serialization.json)
        implementation(libs.kotlinx.serialization.json)
    }
    
    androidMain.dependencies {
        implementation(libs.ktor.client.android)
    }
    
    iosMain.dependencies {
        implementation(libs.ktor.client.ios)
    }
    
    jsMain.dependencies {
        implementation(libs.ktor.client.js)
    }
}

plugins {
    // ... existing plugins ...
    alias(libs.plugins.kotlinxSerialization)  // Add this plugin
}
```

### Step 2: Create Data Models with Serialization

Update `domain/model/Models.kt` to use serialization:

```kotlin
package com.hp.iaiain.features.launching.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EarlyAccessRequest(
    val fullName: String = "",
    val email: String = "",
    val institution: String = "",
    val role: String = "",
    val referralCode: String? = null,
    val agreeToTerms: Boolean = false
)

@Serializable
data class EarlyAccessResponse(
    val success: Boolean,
    val message: String,
    val accessCode: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// ... rest of the models ...
```

### Step 3: Create HTTP Client Configuration

Create `data/network/HttpClientFactory.kt`:

```kotlin
package com.hp.iaiain.features.launching.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(enableLogging: Boolean = true): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            
            if (enableLogging) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
        }
    }
}
```

### Step 4: Update DataSource Implementation

Replace the mock implementation in `data/source/EarlyAccessRemoteDataSource.kt`:

```kotlin
package com.hp.iaiain.features.launching.data.source

import com.hp.iaiain.features.launching.domain.model.EarlyAccessRequest
import com.hp.iaiain.features.launching.domain.model.EarlyAccessResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface EarlyAccessRemoteDataSource {
    suspend fun submitEarlyAccessRequest(request: EarlyAccessRequest): EarlyAccessResponse
    suspend fun validateEmail(email: String): Boolean
}

class EarlyAccessRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.iaiain.com/v1"
) : EarlyAccessRemoteDataSource {
    
    override suspend fun submitEarlyAccessRequest(
        request: EarlyAccessRequest
    ): EarlyAccessResponse {
        return try {
            httpClient.post("$baseUrl/early-access") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            throw Exception("Failed to submit early access request", e)
        }
    }
    
    override suspend fun validateEmail(email: String): Boolean {
        return try {
            val response = httpClient.post("$baseUrl/validate-email") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }.body<Map<String, Boolean>>()
            
            response["valid"] ?: false
        } catch (e: Exception) {
            throw Exception("Failed to validate email", e)
        }
    }
}
```

### Step 5: Update ServiceLocator

Update `di/ServiceLocator.kt` to use real HTTP client:

```kotlin
package com.hp.iaiain.di

import com.hp.iaiain.features.launching.data.network.HttpClientFactory
import com.hp.iaiain.features.launching.data.repository.EarlyAccessRepositoryImpl
import com.hp.iaiain.features.launching.data.source.EarlyAccessRemoteDataSourceImpl
import com.hp.iaiain.features.launching.domain.repository.EarlyAccessRepository
import com.hp.iaiain.features.launching.domain.usecase.SubmitEarlyAccessUseCase
import com.hp.iaiain.features.launching.domain.usecase.ValidateEmailUseCase
import com.hp.iaiain.features.launching.presentation.viewmodel.LaunchingViewModel
import io.ktor.client.HttpClient

object ServiceLocator {
    // HTTP Client
    private val httpClient: HttpClient by lazy {
        HttpClientFactory.create(enableLogging = true)
    }
    
    // Data Sources
    private val earlyAccessRemoteDataSource: EarlyAccessRemoteDataSourceImpl by lazy {
        EarlyAccessRemoteDataSourceImpl(
            httpClient = httpClient,
            baseUrl = "https://api.iaiain.com/v1"  // Configure this externally
        )
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
```

## API Specification

### Early Access Submission

**Endpoint:** `POST /api/v1/early-access`

**Request:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "institution": "MIT",
  "role": "Student",
  "referralCode": "REF123",
  "agreeToTerms": true
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Thank you for your interest! Check your email for next steps.",
  "accessCode": "IAIAIN-123456789",
  "timestamp": 1701945600000
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "Email already registered",
  "accessCode": null,
  "timestamp": 1701945600000
}
```

### Email Validation

**Endpoint:** `POST /api/v1/validate-email`

**Request:**
```json
{
  "email": "john@example.com"
}
```

**Response:**
```json
{
  "valid": true
}
```

## Error Handling

### Network Errors

The repository automatically wraps errors:

```kotlin
class EarlyAccessRepositoryImpl(
    private val remoteDataSource: EarlyAccessRemoteDataSource
) : EarlyAccessRepository {
    
    override suspend fun submitEarlyAccessRequest(
        request: EarlyAccessRequest
    ): Result<EarlyAccessResponse> {
        return try {
            val response = remoteDataSource.submitEarlyAccessRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Handling in ViewModel

```kotlin
private fun submitEarlyAccessRequest() {
    viewModelScope.launch {
        _state.value = _state.value.copy(isSubmitting = true)
        
        val result = submitEarlyAccessUseCase(request)
        
        result.onSuccess { response ->
            _state.value = _state.value.copy(
                isSubmitting = false,
                showSuccessDialog = true,
                successMessage = response.message
            )
        }
        
        result.onFailure { error ->
            _state.value = _state.value.copy(
                isSubmitting = false,
                errorMessage = when {
                    error.message?.contains("Network") == true -> 
                        "Network error. Please check your connection."
                    error.message?.contains("already registered") == true -> 
                        "This email is already registered."
                    else -> error.message ?: "An unexpected error occurred"
                }
            )
        }
    }
}
```

## Environment Configuration

### Multiple Environments

Create `BuildConfig.kt`:

```kotlin
package com.hp.iaiain.config

object BuildConfig {
    enum class Environment {
        DEVELOPMENT, STAGING, PRODUCTION
    }
    
    val currentEnvironment: Environment = Environment.DEVELOPMENT
    
    val apiBaseUrl: String = when (currentEnvironment) {
        Environment.DEVELOPMENT -> "https://api-dev.iaiain.com/v1"
        Environment.STAGING -> "https://api-staging.iaiain.com/v1"
        Environment.PRODUCTION -> "https://api.iaiain.com/v1"
    }
}
```

Update ServiceLocator:

```kotlin
private val earlyAccessRemoteDataSource: EarlyAccessRemoteDataSourceImpl by lazy {
    EarlyAccessRemoteDataSourceImpl(
        httpClient = httpClient,
        baseUrl = BuildConfig.apiBaseUrl
    )
}
```

### Build Variant Configuration

In `composeApp/build.gradle.kts`:

```kotlin
kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
    
    // ... other targets ...
}

// Variant-specific configurations
buildTypes {
    debug {
        buildConfigField("String", "API_BASE_URL", 
            "\"https://api-dev.iaiain.com/v1\"")
    }
    release {
        buildConfigField("String", "API_BASE_URL", 
            "\"https://api.iaiain.com/v1\"")
    }
}
```

## Testing API Integration

### Mock for Testing

```kotlin
class MockEarlyAccessRemoteDataSource : EarlyAccessRemoteDataSource {
    var delayMillis: Long = 0
    var shouldFail: Boolean = false
    var failureMessage: String = "Network error"
    
    override suspend fun submitEarlyAccessRequest(
        request: EarlyAccessRequest
    ): EarlyAccessResponse {
        delay(delayMillis)
        
        if (shouldFail) {
            throw Exception(failureMessage)
        }
        
        return EarlyAccessResponse(
            success = true,
            message = "Success",
            accessCode = "TEST-${System.currentTimeMillis()}"
        )
    }
    
    override suspend fun validateEmail(email: String): Boolean {
        delay(delayMillis)
        if (shouldFail) throw Exception(failureMessage)
        return true
    }
}
```

## Monitoring and Logging

### Enhanced Logging

```kotlin
class LoggingHttpClient(
    private val delegate: HttpClient,
    private val logger: Logger
) : HttpClient by delegate {
    
    // Requests and responses are logged by Ktor's Logging plugin
    // You can configure log level in HttpClientFactory
}
```

### Analytics Integration

```kotlin
private fun submitEarlyAccessRequest() {
    viewModelScope.launch {
        val startTime = System.currentTimeMillis()
        
        val result = submitEarlyAccessUseCase(request)
        val duration = System.currentTimeMillis() - startTime
        
        result.onSuccess {
            AppLogger.info("LaunchingViewModel", 
                "Early access submitted in ${duration}ms")
            // Track event: "early_access_submitted"
        }
        
        result.onFailure {
            AppLogger.error("LaunchingViewModel", 
                "Early access submission failed in ${duration}ms", it)
            // Track event: "early_access_failed"
        }
    }
}
```

## Security Considerations

### HTTPS Only

Ensure all API calls use HTTPS in production.

### Token Management (Future)

```kotlin
class AuthenticationInterceptor(
    private val tokenManager: TokenManager
) : HttpClientPlugin<Unit, Unit> {
    override val key = AttributeKey<Unit>("AuthenticationPlugin")
    
    override fun install(client: HttpClient, configure: Unit.() -> Unit) {
        client.sendPipeline.intercept(HttpRequestPipeline.Before) { request ->
            val token = tokenManager.getToken()
            token?.let {
                request.header("Authorization", "Bearer $it")
            }
        }
    }
}
```

### API Key Management

```kotlin
object ApiKeyProvider {
    private val apiKey = "your-api-key"  // Load from BuildConfig or environment
    
    fun getApiKey(): String = apiKey
}
```

## Debugging

### Enable Verbose Logging

```kotlin
val httpClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL  // HEADERS, BODY, ALL, etc.
        logger = object : Logger {
            override fun log(message: String) {
                println("HTTP: $message")
            }
        }
    }
}
```

### Network Inspector

Android Studio has built-in Network Inspector:
- Run app in debug mode
- Open Profiler → Network tab
- Monitor all HTTP requests and responses

## Troubleshooting

### Common Issues

**Issue:** SSL Certificate Error
```kotlin
// For development only - NOT for production
val httpClient = HttpClient {
    engine {
        // Platform-specific SSL configuration
    }
}
```

**Issue:** Timeout
```kotlin
val httpClient = HttpClient {
    install(HttpTimeout) {
        requestTimeoutMillis = 10000  // 10 seconds
        connectTimeoutMillis = 10000
        socketTimeoutMillis = 10000
    }
}
```

**Issue:** JSON Parsing Error
```kotlin
val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
```

## Summary

✅ Architecture supports seamless API integration
✅ Mock DataSource for development
✅ Easy switch to real API implementation
✅ Built-in error handling and logging
✅ Testable and maintainable

**Next Step:** Update backend API documentation and ensure endpoints match the specification above.

