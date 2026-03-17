package com.ivarna.truvalt.presentation.ui.settings;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u000e\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0018J\u000e\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0018J\u000e\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u0015J\u0006\u0010\u001f\u001a\u00020\u0012J\u0006\u0010 \u001a\u00020\u0012J\u0006\u0010!\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\""}, d2 = {"Lcom/ivarna/truvalt/presentation/ui/settings/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "preferences", "Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;", "authRepository", "Lcom/ivarna/truvalt/domain/repository/AuthRepository;", "syncRepository", "Lcom/ivarna/truvalt/domain/repository/SyncRepository;", "<init>", "(Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;Lcom/ivarna/truvalt/domain/repository/AuthRepository;Lcom/ivarna/truvalt/domain/repository/SyncRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ivarna/truvalt/presentation/ui/settings/SettingsUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadSettings", "", "setBiometricEnabled", "enabled", "", "setAutoLockTimeout", "timeout", "", "setClipboardTimeout", "setTheme", "theme", "", "setLocalOnly", "localOnly", "syncNow", "lockVault", "deleteVault", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.repository.SyncRepository syncRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ivarna.truvalt.presentation.ui.settings.SettingsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.settings.SettingsUiState> uiState = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.repository.SyncRepository syncRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.settings.SettingsUiState> getUiState() {
        return null;
    }
    
    private final void loadSettings() {
    }
    
    public final void setBiometricEnabled(boolean enabled) {
    }
    
    public final void setAutoLockTimeout(long timeout) {
    }
    
    public final void setClipboardTimeout(long timeout) {
    }
    
    public final void setTheme(@org.jetbrains.annotations.NotNull()
    java.lang.String theme) {
    }
    
    public final void setLocalOnly(boolean localOnly) {
    }
    
    public final void syncNow() {
    }
    
    public final void lockVault() {
    }
    
    public final void deleteVault() {
    }
}