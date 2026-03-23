package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.api.BackendApiFactory
import com.ivarna.truvalt.data.remote.dto.BackendAuthRequest
import com.ivarna.truvalt.data.remote.dto.BackendErrorResponse
import com.ivarna.truvalt.domain.repository.AuthRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
    private val preferences: TruvaltPreferences,
    private val backendApiFactory: BackendApiFactory
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
        if (preferences.isLocalOnlySync()) {
            return preferences.encryptedVaultKey.first() != null
        }

        return preferences.getServerUrlSync() != null
    }

    override suspend fun createVault(email: String, password: String): Result<Unit> {
        return try {
            val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
            masterKey = derivedKeys.masterKey
            val authKeyHash = cryptoManager.hashAuthKey(derivedKeys.authKey)

            if (!preferences.isLocalOnlySync()) {
                val serverUrl = preferences.getServerUrlSync()
                    ?: return Result.failure(IllegalStateException("Server URL not configured"))
                val api = backendApiFactory.create(serverUrl)
                val response = withContext(Dispatchers.IO) {
                    api.register(
                        BackendAuthRequest(
                            email = email,
                            password = password,
                            auth_key_hash = authKeyHash
                        )
                    )
                }
                storeBackendSession(response.user.id, response.token, response.refresh_token)
            }
            
            val encryptedVaultKey = cryptoManager.encryptWithKeystore(derivedKeys.vaultKey)
            storeVaultKey(encryptedVaultKey)
            
            preferences.setUserEmail(email)
            preferences.setAuthKeyHash(authKeyHash)
            preferences.setVaultUnlocked(true)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toRepositoryException())
        }
    }

    override suspend fun unlockWithPassword(email: String, password: String): Result<Unit> {
        return try {
            val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
            masterKey = derivedKeys.masterKey
            val authKeyHash = cryptoManager.hashAuthKey(derivedKeys.authKey)

            if (!preferences.isLocalOnlySync()) {
                val serverUrl = preferences.getServerUrlSync()
                    ?: return Result.failure(IllegalStateException("Server URL not configured"))
                val api = backendApiFactory.create(serverUrl)
                val response = withContext(Dispatchers.IO) {
                    api.login(
                        BackendAuthRequest(
                            email = email,
                            password = password,
                            auth_key_hash = authKeyHash
                        )
                    )
                }
                storeBackendSession(response.user.id, response.token, response.refresh_token)
            }

            preferences.setUserEmail(email)
            preferences.setAuthKeyHash(authKeyHash)
            preferences.setVaultUnlocked(true)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toRepositoryException())
        }
    }

    private suspend fun storeBackendSession(userId: String, idToken: String, refreshToken: String?) {
        preferences.setBackendUserId(userId)
        preferences.setBackendIdToken(idToken)
        preferences.setBackendRefreshToken(refreshToken)
    }

    private fun Exception.toRepositoryException(): Exception {
        if (this is HttpException) {
            val raw = response()?.errorBody()?.string()
            val parsed = raw?.let {
                runCatching { Gson().fromJson(it, BackendErrorResponse::class.java) }.getOrNull()
            }
            val backendMessage = parsed?.errors
                ?.values
                ?.firstOrNull()
                ?.firstOrNull()
                ?: parsed?.message

            return IllegalStateException(backendMessage ?: (message ?: "Backend request failed"))
        }

        return this
    }

    fun getMasterKey(): ByteArray? = masterKey

    fun getVaultKey(email: String, password: String): ByteArray {
        val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
        return derivedKeys.vaultKey
    }
}
