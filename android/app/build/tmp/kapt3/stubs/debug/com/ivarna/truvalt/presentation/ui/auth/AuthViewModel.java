package com.ivarna.truvalt.presentation.ui.auth;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ0\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00120\u00142\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00120\u0014J\u0006\u0010\u0017\u001a\u00020\u0012J\u0006\u0010\u0018\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0019"}, d2 = {"Lcom/ivarna/truvalt/presentation/ui/auth/AuthViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lcom/ivarna/truvalt/domain/repository/AuthRepository;", "syncRepository", "Lcom/ivarna/truvalt/domain/repository/SyncRepository;", "preferences", "Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;", "<init>", "(Lcom/ivarna/truvalt/domain/repository/AuthRepository;Lcom/ivarna/truvalt/domain/repository/SyncRepository;Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ivarna/truvalt/presentation/ui/auth/AuthUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "checkAuthState", "", "onHasVault", "Lkotlin/Function0;", "onNoVault", "onHasAccount", "unlockWithBiometric", "clearError", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AuthViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.repository.SyncRepository syncRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ivarna.truvalt.presentation.ui.auth.AuthUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.auth.AuthUiState> uiState = null;
    
    @javax.inject.Inject()
    public AuthViewModel(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.repository.SyncRepository syncRepository, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.auth.AuthUiState> getUiState() {
        return null;
    }
    
    public final void checkAuthState(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onHasVault, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNoVault, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onHasAccount) {
    }
    
    public final void unlockWithBiometric() {
    }
    
    public final void clearError() {
    }
}