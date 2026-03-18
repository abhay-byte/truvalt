package com.ivarna.truvalt.core.lock

import com.ivarna.truvalt.core.crypto.VaultKeyManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    private val vaultKeyManager: VaultKeyManager
) {
    
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()
    
    private var autoLockJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var autoLockTimeoutMs: Long = 60_000L // Default 1 minute
    
    fun setAutoLockTimeout(timeoutMs: Long) {
        autoLockTimeoutMs = timeoutMs
    }
    
    fun unlock() {
        _isLocked.value = false
        resetAutoLockTimer()
    }
    
    fun lock() {
        vaultKeyManager.clearInMemoryKey()
        _isLocked.value = true
        autoLockJob?.cancel()
    }
    
    fun resetAutoLockTimer() {
        autoLockJob?.cancel()
        if (autoLockTimeoutMs > 0) {
            autoLockJob = scope.launch {
                delay(autoLockTimeoutMs)
                lock()
            }
        }
    }
    
    fun startAutoLockCountdown() {
        resetAutoLockTimer()
    }
    
    fun cancelAutoLockCountdown() {
        autoLockJob?.cancel()
    }
}
