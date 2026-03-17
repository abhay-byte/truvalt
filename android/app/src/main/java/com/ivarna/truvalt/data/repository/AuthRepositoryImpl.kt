package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
    private val preferences: TruvaltPreferences
) : AuthRepository {

    private var masterKey: ByteArray? = null

    override suspend fun isVaultUnlocked(): Boolean {
        return masterKey != null && preferences.isVaultUnlocked.first()
    }

    override suspend fun unlockVault(masterKey: ByteArray) {
        this.masterKey = masterKey
        preferences.setVaultUnlocked(true)
    }

    override suspend fun lockVault() {
        masterKey?.fill(0)
        masterKey = null
        preferences.setVaultUnlocked(false)
    }

    override suspend fun isBiometricEnabled(): Boolean {
        return preferences.isBiometricEnabledSync()
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        preferences.setBiometricEnabled(enabled)
    }

    override suspend fun getEncryptedVaultKey(): ByteArray? {
        val encryptedKey = preferences.encryptedVaultKey.first() ?: return null
        return android.util.Base64.decode(encryptedKey, android.util.Base64.DEFAULT)
    }

    override suspend fun storeVaultKey(encryptedKey: ByteArray) {
        val encoded = android.util.Base64.encodeToString(encryptedKey, android.util.Base64.DEFAULT)
        preferences.setEncryptedVaultKey(encoded)
    }

    override suspend fun hasVault(): Boolean {
        return preferences.encryptedVaultKey.first() != null
    }

    override suspend fun createVault(email: String, password: String): Result<Unit> {
        return try {
            val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
            masterKey = derivedKeys.masterKey
            
            val encryptedVaultKey = cryptoManager.encryptWithKeystore(derivedKeys.vaultKey)
            storeVaultKey(encryptedVaultKey)
            
            preferences.setUserEmail(email)
            preferences.setAuthKeyHash(cryptoManager.hashAuthKey(derivedKeys.authKey))
            preferences.setVaultUnlocked(true)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlockWithPassword(email: String, password: String): Result<Unit> {
        return try {
            val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
            masterKey = derivedKeys.masterKey
            preferences.setVaultUnlocked(true)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMasterKey(): ByteArray? = masterKey

    fun getVaultKey(email: String, password: String): ByteArray {
        val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
        return derivedKeys.vaultKey
    }
}
