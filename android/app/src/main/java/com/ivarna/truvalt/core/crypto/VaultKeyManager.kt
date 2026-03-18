package com.ivarna.truvalt.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultKeyManager @Inject constructor() {
    
    private var inMemoryVaultKey: ByteArray? = null
    
    companion object {
        private const val KEY_ALIAS = "truvalt_vault_key_v1"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
    }
    
    fun wrapAndStoreKey(vaultKey: ByteArray): ByteArray {
        val keystoreKey = getOrCreateKeystoreKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keystoreKey)
        val iv = cipher.iv
        val encryptedKey = cipher.doFinal(vaultKey)
        return iv + encryptedKey
    }
    
    fun retrieveAndUnwrapKey(wrappedData: ByteArray): ByteArray {
        val keystoreKey = getOrCreateKeystoreKey()
        val iv = wrappedData.copyOfRange(0, 12)
        val encryptedKey = wrappedData.copyOfRange(12, wrappedData.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keystoreKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(encryptedKey)
    }
    
    fun setInMemoryKey(key: ByteArray) {
        inMemoryVaultKey = key
    }
    
    fun getInMemoryKey(): ByteArray? = inMemoryVaultKey
    
    fun clearInMemoryKey() {
        inMemoryVaultKey?.fill(0)
        inMemoryVaultKey = null
    }
    
    private fun getOrCreateKeystoreKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } else {
            createKeystoreKey()
        }
    }
    
    private fun createKeystoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )
        
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}
