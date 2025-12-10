# Quick Start Guide - IAIAIN Architecture

## Project Structure Created

We've successfully refactored the launching feature following Clean Architecture and MVI pattern.

### ✅ What's Been Implemented

#### 1. **Design System** (`design/system/`)
- **Color.kt**: Comprehensive color palette (primary, secondary, semantic colors)
- **Typography.kt**: Text styles for all UI elements
- **Dimensions.kt**: Reusable spacing and sizing constants
- **Theme.kt**: Material 3 theme composition

#### 2. **Reusable Components** (`design/components/`)
- **Button.kt**: Primary and secondary buttons
- **TextField.kt**: Text input and text area fields with validation
- **Card.kt**: Generic and specialized card components

#### 3. **Domain Layer** (`features/launching/domain/`)
**Purpose**: Business logic independent of framework

- **model/Models.kt**: 
  - `LaunchingState`: Countdown state
  - `EarlyAccessRequest`: Form data model
  - `EarlyAccessResponse`: API response model
  - `LaunchingEffect`: One-time events

- **repository/EarlyAccessRepository.kt**: 
  - Interface defining data access contracts

- **usecase/EarlyAccessUseCase.kt**: 
  - `SubmitEarlyAccessUseCase`: Handles form submission with validation
  - `ValidateEmailUseCase`: Email validation logic

#### 4. **Data Layer** (`features/launching/data/`)
**Purpose**: Data access and API integration

- **source/EarlyAccessRemoteDataSource.kt**: 
  - Interface for remote data operations
  - Mock implementation (ready for real API)

- **repository/EarlyAccessRepositoryImpl.kt**: 
  - Implements domain repository interface
  - Bridges data sources and domain layer

#### 5. **Presentation Layer** (`features/launching/presentation/`)
**Purpose**: UI state management using MVI pattern

- **mvi/LaunchingMVI.kt**: 
  - `LaunchingScreenState`: Current UI state (immutable)
  - `LaunchingIntent`: User actions (sealed class)
  - `LaunchingEffect`: Side effects (one-time events)

- **viewmodel/LaunchingViewModel.kt**: 
  - Handles all intents and state updates
  - Manages countdown timer
  - Orchestrates use cases
  - Emits effects for one-time actions

#### 6. **UI Layer** (`features/launching/ui/composable/`)
**Purpose**: Composable UI elements

- **LaunchingScreen.kt**: 
  - Main screen composition
  - Integrates all sections
  - Dialog handling

- **LaunchingContent.kt**: 
  - Header section with title and description
  - Features showcase cards
  - Countdown display

- **EarlyAccessForm.kt**: 
  - Registration form
  - Input validation UI
  - Terms agreement checkbox

#### 7. **Dependency Injection** (`di/ServiceLocator.kt`)
- Simple service locator for creating ViewModels
- Singleton repositories and use cases
- Ready to be replaced with Hilt/Koin

## How to Use

### Adding a New Feature

1. **Create domain layer** (business logic)
   ```kotlin
   // features/myfeature/domain/usecase/MyUseCase.kt
   class MyUseCase(val repo: MyRepository) {
       suspend operator fun invoke(params: MyParams): Result<MyModel> { }
   }
   ```

2. **Create data layer** (API/database access)
   ```kotlin
   // features/myfeature/data/repository/MyRepositoryImpl.kt
   class MyRepositoryImpl(val dataSource: MyDataSource) : MyRepository { }
   ```

3. **Create presentation layer** (MVI)
   ```kotlin
   // features/myfeature/presentation/viewmodel/MyViewModel.kt
   class MyViewModel(val useCase: MyUseCase) : ViewModel() {
       fun handleIntent(intent: MyIntent) { }
   }
   ```

4. **Create UI layer** (composables)
   ```kotlin
   // features/myfeature/ui/MyScreen.kt
   @Composable
   fun MyScreen(state: MyState, onIntent: (MyIntent) -> Unit) { }
   ```

### Adding a New Component

1. Create it in `design/components/`
2. Use design system tokens (colors, spacing, typography)
3. Make it reusable and parameterizable
4. Example:
   ```kotlin
   @Composable
   fun MyCustomButton(
       text: String,
       onClick: () -> Unit,
       modifier: Modifier = Modifier,
       enabled: Boolean = true
   ) {
       Button(
           onClick = onClick,
           modifier = modifier,
           colors = ButtonDefaults.buttonColors(
               containerColor = AccentOrange
           )
       ) {
           Text(text = text, style = MaterialTheme.typography.labelLarge)
       }
   }
   ```

## Key Files to Understand

1. **LaunchingViewModel.kt**: Shows MVI pattern implementation
2. **LaunchingMVI.kt**: Shows State/Intent/Effect definitions
3. **EarlyAccessRepositoryImpl.kt**: Shows clean architecture layering
4. **LaunchingScreen.kt**: Shows how to compose screens
5. **Theme.kt**: Shows design system usage

## Common Tasks

### Update Countdown Logic
Edit: `LaunchingViewModel.kt` → `startCountdownTimer()` and `updateCountdownTime()`

### Add Form Validation
Edit: `LaunchingViewModel.kt` → `updateEmail()` and other field methods

### Change Colors
Edit: `design/system/Color.kt` and reference constants in components

### Add New Intent Handler
Edit: `LaunchingViewModel.kt` → Add to `handleIntent()` switch statement

### Create Dialog
Use: `ErrorDialog()` and `SuccessDialog()` in `LaunchingScreen.kt` as templates

## Testing Strategy

### Unit Test Use Cases
```kotlin
@Test
fun testSubmitEarlyAccess() {
    val useCase = SubmitEarlyAccessUseCase(mockRepository)
    val result = useCase.invoke(validRequest)
    assertTrue(result.isSuccess)
}
```

### Unit Test ViewModel
```kotlin
@Test
fun testHandleIntent() {
    val viewModel = LaunchingViewModel(mockUseCase, mockValidateUseCase)
    viewModel.handleIntent(LaunchingIntent.UpdateFullName("John"))
    assertEquals("John", viewModel.state.value.fullName)
}
```

### Compose UI Tests
```kotlin
@Test
fun testLaunchingScreenRenders() {
    composeTestRule.setContent {
        LaunchingScreen(initialState) { }
    }
    composeTestRule.onNodeWithText("Something Amazing").assertExists()
}
```

## API Integration

To connect to real API:

1. **Install HTTP client** (Ktor recommended):
   ```toml
   # gradle/libs.versions.toml
   ktor-client = "2.x.x"
   ```

2. **Update DataSource**:
   ```kotlin
   // data/source/EarlyAccessRemoteDataSource.kt
   class EarlyAccessRemoteDataSourceImpl(val client: HttpClient) {
       override suspend fun submitEarlyAccessRequest(request: EarlyAccessRequest) {
           val response = client.post("api/early-access") {
               contentType(ContentType.Application.Json)
               setBody(request)
           }
           return response.body()
       }
   }
   ```

## Next Steps

1. ✅ Design System established
2. ✅ MVI pattern implemented
3. ✅ Clean Architecture structure created
4. ⏭️ Connect to real API
5. ⏭️ Add more screens/features
6. ⏭️ Implement navigation
7. ⏭️ Add database persistence
8. ⏭️ Comprehensive testing

## Support

For questions about the architecture:
- See `ARCHITECTURE.md` for detailed explanation
- Check existing code examples
- Follow the patterns established in LaunchingViewModel

Happy coding! 🚀

