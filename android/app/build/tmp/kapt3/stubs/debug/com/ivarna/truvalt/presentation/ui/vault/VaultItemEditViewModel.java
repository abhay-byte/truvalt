package com.ivarna.truvalt.presentation.ui.vault;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J8\u0010\u0011\u001a\u00020\u000e2\b\u0010\u0012\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u0010J\u000e\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0019\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u001a"}, d2 = {"Lcom/ivarna/truvalt/presentation/ui/vault/VaultItemEditViewModel;", "Landroidx/lifecycle/ViewModel;", "vaultRepository", "Lcom/ivarna/truvalt/domain/repository/VaultRepository;", "<init>", "(Lcom/ivarna/truvalt/domain/repository/VaultRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ivarna/truvalt/presentation/ui/vault/VaultItemEditUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadItem", "", "itemId", "", "saveLoginItem", "id", "name", "url", "username", "password", "notes", "deleteItem", "clearError", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class VaultItemEditViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.repository.VaultRepository vaultRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ivarna.truvalt.presentation.ui.vault.VaultItemEditUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.vault.VaultItemEditUiState> uiState = null;
    
    @javax.inject.Inject()
    public VaultItemEditViewModel(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.repository.VaultRepository vaultRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.ivarna.truvalt.presentation.ui.vault.VaultItemEditUiState> getUiState() {
        return null;
    }
    
    public final void loadItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId) {
    }
    
    public final void saveLoginItem(@org.jetbrains.annotations.Nullable()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String notes) {
    }
    
    public final void deleteItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId) {
    }
    
    public final void clearError() {
    }
}