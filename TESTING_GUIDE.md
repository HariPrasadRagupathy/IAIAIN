# Testing Guide - IAIAIN Architecture

## Testing Strategy Overview

The architecture supports multiple levels of testing to ensure code quality and reliability.

### Testing Pyramid

```
         ╱╲
        ╱  ╲        E2E Tests (10%)
       ╱────╲       - Full app flows
      ╱  UI  ╲      - Real device testing
     ╱────────╲
    ╱          ╲    Integration Tests (20%)
   ╱  ViewModel ╲  - ViewModel + UseCase
  ╱──────────────╲  - Mock repositories
 ╱                ╲ 
╱  Unit Tests (70%)╲
────────────────────
- UseCase tests
- Repository tests
- Validator tests
- Extension functions
```

## Unit Testing

### Testing Use Cases

```kotlin
@Test
fun `submitEarlyAccess with valid request returns success`() {
    // Arrange
    val mockRepository = mockk<EarlyAccessRepository>()
    coEvery { mockRepository.submitEarlyAccessRequest(any()) } returns 
        Result.success(EarlyAccessResponse(true, "Success", "CODE123"))
    
    val useCase = SubmitEarlyAccessUseCase(mockRepository)
    val validRequest = EarlyAccessRequest(
        fullName = "John Doe",
        email = "john@example.com",
        institution = "MIT",
        role = "Student",
        agreeToTerms = true
    )
    
    // Act
    val result = useCase(validRequest)
    
    // Assert
    assertTrue(result.isSuccess)
    assertEquals("CODE123", result.getOrNull()?.accessCode)
    coVerify { mockRepository.submitEarlyAccessRequest(validRequest) }
}

@Test
fun `submitEarlyAccess with blank name returns failure`() {
    // Arrange
    val mockRepository = mockk<EarlyAccessRepository>()
    val useCase = SubmitEarlyAccessUseCase(mockRepository)
    val invalidRequest = EarlyAccessRequest(
        fullName = "",  // Invalid
        email = "john@example.com",
        institution = "MIT",
        role = "Student",
        agreeToTerms = true
    )
    
    // Act
    val result = useCase(invalidRequest)
    
    // Assert
    assertTrue(result.isFailure)
    assertEquals("Full name is required", result.exceptionOrNull()?.message)
}

@Test
fun `validateEmail with invalid format returns failure`() {
    // Arrange
    val mockRepository = mockk<EarlyAccessRepository>()
    val useCase = ValidateEmailUseCase(mockRepository)
    
    // Act
    val result = useCase("invalid-email")
    
    // Assert
    assertTrue(result.isFailure)
}
```

### Testing Validators

```kotlin
@Test
fun `EmailValidator validates correct format`() {
    val result = EmailValidator.validate("user@example.com")
    assertIs<ValidationResult.Valid>(result)
}

@Test
fun `EmailValidator rejects invalid format`() {
    val result = EmailValidator.validate("invalid.email")
    assertIs<ValidationResult.Invalid>(result)
}

@Test
fun `NameValidator rejects short names`() {
    val result = NameValidator.validate("A")
    val error = (result as ValidationResult.Invalid).errorMessage
    assertTrue(error.contains("at least 2 characters"))
}

@Test
fun `FieldValidator rejects blank fields`() {
    val result = FieldValidator.validate("", "Email")
    val error = (result as ValidationResult.Invalid).errorMessage
    assertTrue(error.contains("Email is required"))
}
```

## ViewModel Testing

### Testing State Updates

```kotlin
@Test
fun `updateFullName updates state correctly`() {
    // Arrange
    val mockSubmitUseCase = mockk<SubmitEarlyAccessUseCase>()
    val mockValidateUseCase = mockk<ValidateEmailUseCase>()
    val viewModel = LaunchingViewModel(mockSubmitUseCase, mockValidateUseCase)
    
    // Act
    viewModel.handleIntent(LaunchingIntent.UpdateFullName("John Doe"))
    
    // Assert
    assertEquals("John Doe", viewModel.state.value.fullName)
}

@Test
fun `updateEmail sets error on invalid format`() {
    // Arrange
    val mockSubmitUseCase = mockk<SubmitEarlyAccessUseCase>()
    val mockValidateUseCase = mockk<ValidateEmailUseCase>()
    val viewModel = LaunchingViewModel(mockSubmitUseCase, mockValidateUseCase)
    
    // Act
    viewModel.handleIntent(LaunchingIntent.UpdateEmail("invalid"))
    
    // Assert
    assertNotNull(viewModel.state.value.emailError)
}

@Test
fun `submitEarlyAccessRequest calls use case with correct data`() {
    // Arrange
    val mockSubmitUseCase = mockk<SubmitEarlyAccessUseCase>()
    val mockValidateUseCase = mockk<ValidateEmailUseCase>()
    coEvery { mockSubmitUseCase(any()) } returns 
        Result.success(EarlyAccessResponse(true, "Success", "CODE"))
    
    val viewModel = LaunchingViewModel(mockSubmitUseCase, mockValidateUseCase)
    
    // Set up valid form data
    viewModel.handleIntent(LaunchingIntent.UpdateFullName("John Doe"))
    viewModel.handleIntent(LaunchingIntent.UpdateEmail("john@example.com"))
    viewModel.handleIntent(LaunchingIntent.UpdateInstitution("MIT"))
    viewModel.handleIntent(LaunchingIntent.UpdateRole("Student"))
    viewModel.handleIntent(LaunchingIntent.UpdateAgreeToTerms(true))
    
    // Act
    viewModel.handleIntent(LaunchingIntent.SubmitEarlyAccessRequest)
    
    // Assert
    coVerify { mockSubmitUseCase(any()) }
}
```

## Integration Testing

### Testing ViewModel with UseCase

```kotlin
@Test
fun `LaunchingViewModel integrates with SubmitEarlyAccessUseCase`() = runTest {
    // Arrange
    val mockRepository = mockk<EarlyAccessRepository>()
    coEvery { mockRepository.submitEarlyAccessRequest(any()) } returns 
        Result.success(EarlyAccessResponse(true, "Success", "CODE"))
    
    val submitUseCase = SubmitEarlyAccessUseCase(mockRepository)
    val validateUseCase = ValidateEmailUseCase(mockRepository)
    val viewModel = LaunchingViewModel(submitUseCase, validateUseCase)
    
    // Set up form data
    viewModel.handleIntent(LaunchingIntent.UpdateFullName("John Doe"))
    viewModel.handleIntent(LaunchingIntent.UpdateEmail("john@example.com"))
    viewModel.handleIntent(LaunchingIntent.UpdateInstitution("MIT"))
    viewModel.handleIntent(LaunchingIntent.UpdateRole("Student"))
    viewModel.handleIntent(LaunchingIntent.UpdateAgreeToTerms(true))
    
    // Act
    viewModel.handleIntent(LaunchingIntent.SubmitEarlyAccessRequest)
    
    // Assert
    assertEquals(true, viewModel.state.value.showSuccessDialog)
    assertEquals("Success", viewModel.state.value.successMessage)
}
```

## Composition Testing (Jetpack Compose)

### Testing Composables

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun `LaunchingScreen renders all sections`() {
    // Arrange
    val initialState = LaunchingScreenState()
    
    // Act
    composeTestRule.setContent {
        IAIAINTheme {
            LaunchingScreen(initialState) { }
        }
    }
    
    // Assert
    composeTestRule
        .onNodeWithText("Something Amazing")
        .assertExists()
    
    composeTestRule
        .onNodeWithText("Get Early Access")
        .assertExists()
    
    composeTestRule
        .onNodeWithText("Launch Countdown")
        .assertExists()
}

@Test
fun `EarlyAccessForm updates state on input`() {
    // Arrange
    val initialState = LaunchingScreenState()
    var capturedIntent: LaunchingIntent? = null
    
    // Act
    composeTestRule.setContent {
        IAIAINTheme {
            EarlyAccessSection(
                fullName = initialState.fullName,
                onFullNameChange = { name ->
                    capturedIntent = LaunchingIntent.UpdateFullName(name)
                },
                // ... other parameters
            )
        }
    }
    
    composeTestRule
        .onNodeWithContentDescription("Full Name")
        .performTextInput("John Doe")
    
    // Assert
    assertIs<LaunchingIntent.UpdateFullName>(capturedIntent)
    assertEquals("John Doe", (capturedIntent as LaunchingIntent.UpdateFullName).name)
}

@Test
fun `PrimaryButton shows loading state`() {
    // Act
    composeTestRule.setContent {
        IAIAINTheme {
            PrimaryButton(
                text = "Submit",
                onClick = { },
                isLoading = true
            )
        }
    }
    
    // Assert - Button should be disabled and show loading indicator
    composeTestRule
        .onNodeWithText("Submit")
        .assertIsNotEnabled()
}
```

## Test Fixtures and Builders

### Creating Test Data

```kotlin
object LaunchingTestDataBuilder {
    fun buildValidEarlyAccessRequest(
        fullName: String = "John Doe",
        email: String = "john@example.com",
        institution: String = "MIT",
        role: String = "Student",
        referralCode: String? = null,
        agreeToTerms: Boolean = true
    ) = EarlyAccessRequest(
        fullName = fullName,
        email = email,
        institution = institution,
        role = role,
        referralCode = referralCode,
        agreeToTerms = agreeToTerms
    )
    
    fun buildValidLaunchingScreenState(
        countdownDays: Int = 30,
        fullName: String = "John Doe",
        email: String = "john@example.com"
    ) = LaunchingScreenState(
        countdownDays = countdownDays,
        fullName = fullName,
        email = email
    )
}

// Usage in tests
@Test
fun testWithValidData() {
    val request = LaunchingTestDataBuilder.buildValidEarlyAccessRequest()
    // ... test code
}
```

## Mock Repository Implementation

```kotlin
class FakeEarlyAccessRepository : EarlyAccessRepository {
    var shouldSucceed = true
    var successResponse = EarlyAccessResponse(true, "Success", "CODE")
    var failureException = Exception("Network error")
    
    override suspend fun submitEarlyAccessRequest(
        request: EarlyAccessRequest
    ): Result<EarlyAccessResponse> {
        return if (shouldSucceed) {
            Result.success(successResponse)
        } else {
            Result.failure(failureException)
        }
    }
    
    override suspend fun validateEmail(email: String): Result<Boolean> {
        return Result.success(true)
    }
}

// Usage
@Test
fun testWithFailingRepository() {
    val repository = FakeEarlyAccessRepository().apply {
        shouldSucceed = false
    }
    // ... test code
}
```

## Running Tests

### Command Line

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests LaunchingViewModelTest

# Run specific test method
./gradlew test --tests LaunchingViewModelTest.testUpdateFullName

# Run with coverage
./gradlew testDebugUnitTestCoverage

# Run compose UI tests
./gradlew connectedAndroidTest
```

### IDE Integration

Most IDEs (Android Studio, IntelliJ) support:
- Right-click on test class → Run
- Right-click on test method → Run
- Gutter icons for quick run
- Debug mode with breakpoints

## Test Coverage Goals

```
Target Coverage by Layer:
- Domain (Use Cases): 90%+
- Data (Repository): 85%+
- Presentation (ViewModel): 80%+
- UI (Composables): 70%+
- Overall: 80%+
```

## Best Practices

✅ **DO:**
- Test behavior, not implementation
- Use meaningful test names (testXxxReturnsYyyWhenZzz)
- Arrange-Act-Assert pattern
- One assertion per test (ideally)
- Mock external dependencies
- Test edge cases and error conditions
- Use test fixtures and builders
- Keep tests independent

❌ **DON'T:**
- Test private methods directly
- Create tight coupling to implementation
- Skip error case testing
- Make tests dependent on execution order
- Test multiple scenarios in one test
- Use sleep() in tests (use real/fake time)
- Ignore test warnings

## Continuous Integration

Create `.github/workflows/test.yml`:

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - run: ./gradlew test --no-daemon
      - run: ./gradlew jacocoTestReport
      - uses: codecov/codecov-action@v3
```

## Useful Testing Libraries

- **Kotest**: Advanced assertions
- **Mockk**: Mocking library for Kotlin
- **Turbine**: Testing Flows and StateFlows
- **TestDispatchers**: Coroutine testing
- **Compose Testing**: UI testing for Compose
- **Jacoco**: Code coverage reporting

---

**Happy Testing!** 🧪

