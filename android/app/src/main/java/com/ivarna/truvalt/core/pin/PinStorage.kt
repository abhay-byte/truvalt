package com.ivarna.truvalt.core.pin

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "truvalt_pin_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_PIN_ENABLED = "pin_enabled"
        private const val KEY_FAIL_COUNT = "pin_fail_count"
        private const val KEY_PIN_LENGTH = "pin_length"
    }
    
    fun saveHash(hash: String, salt: ByteArray, pinLength: Int) {
        sharedPreferences.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, Base64.getEncoder().encodeToString(salt))
            .putBoolean(KEY_PIN_ENABLED, true)
            .putInt(KEY_PIN_LENGTH, pinLength)
            .apply()
    }
    
    fun getHash(): String? = sharedPreferences.getString(KEY_PIN_HASH, null)
    
    fun getSalt(): ByteArray? {
        val saltString = sharedPreferences.getString(KEY_PIN_SALT, null) ?: return null
        return Base64.getDecoder().decode(saltString)
    }
    
    fun getPinLength(): Int {
        val stored = sharedPreferences.getInt(KEY_PIN_LENGTH, -1)
        // If not stored (old PINs), default to 4 for backward compatibility
        return if (stored == -1) 4 else stored
    }
    
    fun isEnabled(): Boolean = sharedPreferences.getBoolean(KEY_PIN_ENABLED, false)
    
    fun clear() {
        sharedPreferences.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .putBoolean(KEY_PIN_ENABLED, false)
            .remove(KEY_FAIL_COUNT)
            .remove(KEY_PIN_LENGTH)
            .apply()
    }
    
    fun getFailCount(): Int = sharedPreferences.getInt(KEY_FAIL_COUNT, 0)
    
    fun incrementFailCount() {
        val current = getFailCount()
        sharedPreferences.edit().putInt(KEY_FAIL_COUNT, current + 1).apply()
    }
    
    fun resetFailCount() {
        sharedPreferences.edit().putInt(KEY_FAIL_COUNT, 0).apply()
    }
}
