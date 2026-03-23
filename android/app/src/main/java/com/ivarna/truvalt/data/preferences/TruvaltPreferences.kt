package com.ivarna.truvalt.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "truvalt_preferences")

@Singleton
class TruvaltPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val IS_LOCAL_ONLY = booleanPreferencesKey("is_local_only")
        private val IS_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val CLIPBOARD_TIMEOUT = longPreferencesKey("clipboard_timeout")
        private val AUTO_LOCK_TIMEOUT = longPreferencesKey("auto_lock_timeout")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        private val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        private val IS_VAULT_UNLOCKED = booleanPreferencesKey("is_vault_unlocked")
        private val ENCRYPTED_VAULT_KEY = stringPreferencesKey("encrypted_vault_key")
        private val WRAPPED_VAULT_KEY = stringPreferencesKey("wrapped_vault_key")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val AUTH_KEY_HASH = stringPreferencesKey("auth_key_hash")
        private val BACKEND_ID_TOKEN = stringPreferencesKey("backend_id_token")
        private val BACKEND_REFRESH_TOKEN = stringPreferencesKey("backend_refresh_token")
        private val BACKEND_USER_ID = stringPreferencesKey("backend_user_id")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    val serverUrl: Flow<String?> = context.dataStore.data.map { it[SERVER_URL] }
    val isLocalOnly: Flow<Boolean> = context.dataStore.data.map { it[IS_LOCAL_ONLY] ?: false }
    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[IS_BIOMETRIC_ENABLED] ?: false }
    val clipboardTimeout: Flow<Long> = context.dataStore.data.map { it[CLIPBOARD_TIMEOUT] ?: 30L }
    val autoLockTimeout: Flow<Long> = context.dataStore.data.map { it[AUTO_LOCK_TIMEOUT] ?: 300000L }
    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "system" }
    val lastSyncTime: Flow<Long> = context.dataStore.data.map { it[LAST_SYNC_TIME] ?: 0L }
    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { it[IS_ONBOARDING_COMPLETE] ?: false }
    val isVaultUnlocked: Flow<Boolean> = context.dataStore.data.map { it[IS_VAULT_UNLOCKED] ?: false }
    val encryptedVaultKey: Flow<String?> = context.dataStore.data.map { 
        it[ENCRYPTED_VAULT_KEY]?.takeIf { key -> key.isNotEmpty() }
    }
    val wrappedVaultKey: Flow<String?> = context.dataStore.data.map { 
        it[WRAPPED_VAULT_KEY]?.takeIf { key -> key.isNotEmpty() }
    }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val authKeyHash: Flow<String?> = context.dataStore.data.map { it[AUTH_KEY_HASH] }
    val backendIdToken: Flow<String?> = context.dataStore.data.map { it[BACKEND_ID_TOKEN] }
    val backendRefreshToken: Flow<String?> = context.dataStore.data.map { it[BACKEND_REFRESH_TOKEN] }
    val backendUserId: Flow<String?> = context.dataStore.data.map { it[BACKEND_USER_ID] }
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { it[IS_FIRST_LAUNCH] ?: true }

    suspend fun setServerUrl(url: String?) {
        context.dataStore.edit { it[SERVER_URL] = url ?: "" }
    }

    suspend fun setLocalOnly(localOnly: Boolean) {
        context.dataStore.edit { it[IS_LOCAL_ONLY] = localOnly }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[IS_BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setClipboardTimeout(seconds: Long) {
        context.dataStore.edit { it[CLIPBOARD_TIMEOUT] = seconds }
    }

    suspend fun setAutoLockTimeout(millis: Long) {
        context.dataStore.edit { it[AUTO_LOCK_TIMEOUT] = millis }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setLastSyncTime(time: Long) {
        context.dataStore.edit { it[LAST_SYNC_TIME] = time }
    }
    
    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { it[IS_FIRST_LAUNCH] = isFirst }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[IS_ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setVaultUnlocked(unlocked: Boolean) {
        context.dataStore.edit { it[IS_VAULT_UNLOCKED] = unlocked }
    }

    suspend fun setEncryptedVaultKey(key: String?) {
        context.dataStore.edit {
            if (key != null) it[ENCRYPTED_VAULT_KEY] = key
            else it.remove(ENCRYPTED_VAULT_KEY)
        }
    }

    suspend fun setWrappedVaultKey(keyBytes: ByteArray) {
        val base64 = android.util.Base64.encodeToString(keyBytes, android.util.Base64.NO_WRAP)
        context.dataStore.edit { it[WRAPPED_VAULT_KEY] = base64 }
    }

    suspend fun getWrappedVaultKey(): ByteArray? {
        val base64 = wrappedVaultKey.first() ?: return null
        return android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
    }

    suspend fun setUserEmail(email: String?) {
        context.dataStore.edit {
            if (email != null) it[USER_EMAIL] = email
            else it.remove(USER_EMAIL)
        }
    }

    suspend fun setAuthKeyHash(hash: String?) {
        context.dataStore.edit {
            if (hash != null) it[AUTH_KEY_HASH] = hash
            else it.remove(AUTH_KEY_HASH)
        }
    }

    suspend fun setBackendIdToken(token: String?) {
        context.dataStore.edit {
            if (token != null) it[BACKEND_ID_TOKEN] = token
            else it.remove(BACKEND_ID_TOKEN)
        }
    }

    suspend fun setBackendRefreshToken(token: String?) {
        context.dataStore.edit {
            if (token != null) it[BACKEND_REFRESH_TOKEN] = token
            else it.remove(BACKEND_REFRESH_TOKEN)
        }
    }

    suspend fun setBackendUserId(userId: String?) {
        context.dataStore.edit {
            if (userId != null) it[BACKEND_USER_ID] = userId
            else it.remove(BACKEND_USER_ID)
        }
    }

    suspend fun clearVaultData() {
        context.dataStore.edit {
            it.remove(ENCRYPTED_VAULT_KEY)
            it.remove(USER_EMAIL)
            it.remove(AUTH_KEY_HASH)
            it.remove(BACKEND_ID_TOKEN)
            it.remove(BACKEND_REFRESH_TOKEN)
            it.remove(BACKEND_USER_ID)
            it[IS_VAULT_UNLOCKED] = false
        }
    }

    suspend fun getServerUrlSync(): String? = serverUrl.first().takeIf { it?.isNotEmpty() == true }
    suspend fun isLocalOnlySync(): Boolean = isLocalOnly.first()
    suspend fun isBiometricEnabledSync(): Boolean = isBiometricEnabled.first()
    suspend fun getBackendIdTokenSync(): String? = backendIdToken.first().takeIf { it?.isNotEmpty() == true }
    suspend fun getBackendRefreshTokenSync(): String? = backendRefreshToken.first().takeIf { it?.isNotEmpty() == true }
    suspend fun getBackendUserIdSync(): String? = backendUserId.first().takeIf { it?.isNotEmpty() == true }
}
