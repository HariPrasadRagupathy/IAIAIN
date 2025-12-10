# IAIAIN - Clean Architecture & MVI Implementation

## Architecture Overview

This project implements a modern Android/Multiplatform architecture following Clean Architecture and Model-View-Intent (MVI) principles.

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                     UI Layer (Composables)                   │
│              (LaunchingScreen, Components, Theme)            │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                  Presentation Layer (MVI)                    │
│         (ViewModel, State, Intent, Effect, Reducer)          │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                    Domain Layer                              │
│        (UseCases, Repositories, Models, Entities)            │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                     Data Layer                               │
│      (Repository Implementations, DataSources, APIs)         │
└─────────────────────────────────────────────────────────────┘
```

## Directory Structure

```
src/commonMain/kotlin/com/hp/iaiain/
├── design/
│   └── system/
│       ├── Color.kt              # Color palette
│       ├── Typography.kt         # Text styles
│       ├── Dimensions.kt         # Spacing, sizes
│       └── Theme.kt              # Theme composition
│   └── components/
│       ├── Button.kt             # Reusable button components
│       ├── TextField.kt          # Input fields
│       └── Card.kt               # Card components
├── features/
│   └── launching/
│       ├── data/
│       │   ├── source/
│       │   │   └── EarlyAccessRemoteDataSource.kt
│       │   └── repository/
│       │       └── EarlyAccessRepositoryImpl.kt
│       ├── domain/
│       │   ├── model/
│       │   │   └── Models.kt
│       │   ├── repository/
│       │   │   └── EarlyAccessRepository.kt
│       │   └── usecase/
│       │       └── EarlyAccessUseCase.kt
│       ├── presentation/
│       │   ├── mvi/
│       │   │   └── LaunchingMVI.kt    # State, Intent, Effect
│       │   └── viewmodel/
│       │       └── LaunchingViewModel.kt
│       └── ui/
│           └── composable/
│               ├── LaunchingScreen.kt    # Main screen
│               ├── LaunchingContent.kt   # Content sections
│               └── EarlyAccessForm.kt    # Form component
├── di/
│   └── ServiceLocator.kt          # Dependency injection
└── App.kt                         # Main app entry point
```

## MVI Pattern Explanation

### Model (State)
Represents the current UI state immutably.

```kotlin
data class LaunchingScreenState(
    val countdownDays: Int = 0,
    val fullName: String = "",
    val email: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    // ... more fields
)
```

### View (Intent)
User actions that trigger state changes.

```kotlin
sealed class LaunchingIntent {
    data class UpdateFullName(val name: String) : LaunchingIntent()
    data object SubmitEarlyAccessRequest : LaunchingIntent()
    // ... more intents
}
```

### Reducer/ViewModel
Processes intents and updates state.

```kotlin
fun handleIntent(intent: LaunchingIntent) {
    when (intent) {
        is LaunchingIntent.UpdateFullName -> updateFullName(intent.name)
        is LaunchingIntent.SubmitEarlyAccessRequest -> submitRequest()
    }
}
```

### Effect
One-time events that should be handled by the UI.

```kotlin
sealed class LaunchingEffect {
    data class ShowSuccess(val message: String) : LaunchingEffect()
    data class ShowError(val message: String) : LaunchingEffect()
    data class OpenLink(val url: String) : LaunchingEffect()
}
```

## Clean Architecture Benefits

### 1. **Separation of Concerns**
- Each layer has a specific responsibility
- UI doesn't directly access data
- Business logic is independent of framework

### 2. **Testability**
- Mock repositories for testing ViewModels
- Mock ViewModels for testing UI
- Use cases can be tested in isolation

### 3. **Reusability**
- Domain layer can be shared across platforms
- Components can be used in multiple screens
- Use cases can be reused by different features

### 4. **Maintainability**
- Clear code organization
- Easy to locate and modify functionality
- Reduced coupling between components

## Design System

### Color Palette
- **Primary**: Dark blue (#1B3A6B)
- **Secondary**: Orange (#FFA500)
- **Tertiary**: Purple (#9B59B6)
- **Success**: Green (#2ED573)

### Spacing Scale
```
xs   = 4.dp
sm   = 8.dp
md   = 12.dp
lg   = 16.dp
xl   = 24.dp
xxl  = 32.dp
xxxl = 48.dp
```

### Typography
- **Headline**: 32sp (Bold) - Page titles
- **Title**: 22sp (SemiBold) - Section titles
- **Body**: 16sp (Regular) - Main content
- **Label**: 14sp (Medium) - Form labels, buttons

## Usage Example

### Creating a Component
```kotlin
@Composable
fun MyComponent(
    title: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite
        )
        
        PrimaryButton(
            text = "Action",
            onClick = onActionClick
        )
    }
}
```

### Using the ViewModel
```kotlin
@Composable
fun MyScreen() {
    val viewModel = remember { ServiceLocator.createLaunchingViewModel() }
    val state = viewModel.state.collectAsState()
    
    LaunchingScreen(
        state = state.value,
        onIntent = { viewModel.handleIntent(it) }
    )
}
```

## Data Flow

```
User Action (Tap Button)
         ↓
    LaunchingIntent
         ↓
    ViewModel.handleIntent()
         ↓
    UseCase execution
         ↓
    Repository call
         ↓
    DataSource (API/DB)
         ↓
    Result returned
         ↓
    State updated
         ↓
    UI recomposes
```

## Next Steps

1. **Integrate with Real API**: Replace mock DataSource with actual API calls
2. **Add Navigation**: Implement navigation between screens
3. **State Persistence**: Add ViewModel state saving/restoration
4. **Error Handling**: Implement comprehensive error handling
5. **Testing**: Add unit and integration tests
6. **Logging**: Implement logging for debugging

## Code Structure Best Practices

✅ **DO:**
- Keep composables focused and small
- Use composition over inheritance
- Pass data down, events up
- Keep business logic in ViewModels/UseCases
- Use immutable data classes for state

❌ **DON'T:**
- Modify mutable state directly
- Put business logic in composables
- Create ViewModels inside composables
- Ignore error handling
- Use var for state (use val with copy())

## Dependencies

- Kotlin Multiplatform
- Jetpack Compose
- AndroidX Lifecycle
- Coroutines (for async operations)

## License

Proprietary - HP IAIAIN

