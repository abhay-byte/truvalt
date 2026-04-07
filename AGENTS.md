## Android Development Rules

- Before creating any screen, ViewModel, or repository → call $android-development skill
- Before writing any Composable → call $claude-android-ninja skill
- Before setting up Gradle, version catalog, or build logic → call $claude-android-ninja skill
- Before writing coroutines, Flow, or async code → call $android-development skill
- Always use StateFlow (never LiveData), collectAsStateWithLifecycle (never collectAsState)
- Always follow MVVM/MVI with unidirectional data flow
- Always use Hilt for DI, Room for local persistence, Retrofit for networking
- Never access repository directly from ViewModel — always go through UseCase
