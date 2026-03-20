package com.ivarna.truvalt.core.lock

import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import com.ivarna.truvalt.domain.repository.VaultRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    private val vaultKeyManager: VaultKeyManager,
    private val preferences: TruvaltPreferences,
    private val vaultRepository: VaultRepository
) {
    
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()
    
    private var autoLockJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    fun unlock() {
        _isLocked.value = false
    }
    
    fun lock() {
        vaultKeyManager.clearInMemoryKey()
        (vaultRepository as? VaultRepositoryImpl)?.clearVaultKey()
        _isLocked.value = true
        autoLockJob?.cancel()
    }
    
    fun startAutoLockCountdown() {
        autoLockJob?.cancel()
        autoLockJob = scope.launch {
            val timeout = preferences.autoLockTimeout.first()
            if (timeout > 0) {
                delay(timeout)
                lock()
            }
        }
    }
    
    fun cancelAutoLockCountdown() {
        autoLockJob?.cancel()
    }
}
