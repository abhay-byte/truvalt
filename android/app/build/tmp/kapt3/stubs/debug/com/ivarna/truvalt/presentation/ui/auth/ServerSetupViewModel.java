package com.ivarna.truvalt.presentation.ui.auth;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0014"}, d2 = {"Lcom/ivarna/truvalt/presentation/ui/auth/ServerSetupViewModel;", "Landroidx/lifecycle/ViewModel;", "syncRepository", "Lcom/ivarna/truvalt/domain/repository/SyncRepository;", "<init>", "(Lcom/ivarna/truvalt/domain/repository/SyncRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ivarna/truvalt/presentation/ui/auth/ServerSetupUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "saveServerConfig", "", "serverUrl", "", "useLocalOnly", "", "clearError", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ServerSetupViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.repository.SyncRepository syncRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ivarna.truvalt.presentation.ui.auth.ServerSetupUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.auth.ServerSetupUiState> uiState = null;
    
    @javax.inject.Inject()
    public ServerSetupViewModel(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.repository.SyncRepository syncRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.auth.ServerSetupUiState> getUiState() {
        return null;
    }
    
    public final void saveServerConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String serverUrl, boolean useLocalOnly) {
    }
    
    public final void clearError() {
    }
}