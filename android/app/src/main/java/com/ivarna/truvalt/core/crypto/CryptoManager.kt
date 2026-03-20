package com.ivarna.truvalt.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import android.util.Base64
import kotlin.io.encoding.Base64 as KotlinBase64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "truvalt_master_key"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
        private const val ARGON2_MEMORY = 65536
        private const val ARGON2_ITERATIONS = 3
        private const val ARGON2_PARALLELISM = 4
        private const val KEY_LENGTH = 256
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    private val secureRandom = SecureRandom()

    fun deriveKeyFromPassword(password: String, email: String): DerivedKeys {
        val salt = deriveSalt(email)
        
        val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withMemoryAsKB(ARGON2_MEMORY)
            .withIterations(ARGON2_ITERATIONS)
            .withParallelism(ARGON2_PARALLELISM)
            .withSalt(salt)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(params)

        val masterKey = ByteArray(KEY_LENGTH / 8)
        generator.generateBytes(password.toCharArray(), masterKey)

        val authKey = hkdf(masterKey, "auth".toByteArray())
        val vaultKey = hkdf(masterKey, "vault".toByteArray())

        return DerivedKeys(
            masterKey = masterKey,
            authKey = authKey,
            vaultKey = vaultKey,
            salt = salt
        )
    }

    fun deriveKey(password: String): ByteArray {
        val salt = "truvalt-local-only".toByteArray()
        
        val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withMemoryAsKB(ARGON2_MEMORY)
            .withIterations(ARGON2_ITERATIONS)
            .withParallelism(ARGON2_PARALLELISM)
            .withSalt(salt)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(params)

        val vaultKey = ByteArray(KEY_LENGTH / 8)
        generator.generateBytes(password.toCharArray(), vaultKey)

        return vaultKey
    }

    private fun deriveSalt(email: String): ByteArray {
        val normalizedEmail = email.lowercase().trim()
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(normalizedEmail.toByteArray(Charsets.UTF_8))
    }

    private fun hkdf(inputKey: ByteArray, info: ByteArray): ByteArray {
        val prk = hmacSha256(inputKey, info + ByteArray(1))
        val okm = hmacSha256(prk, ByteArray(0) + 0x01)
        return okm.take(KEY_LENGTH / 8).toByteArray()
    }

    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val cipher = javax.crypto.Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key, "HmacSHA256")
        cipher.init(secretKey)
        return cipher.doFinal(data)
    }

    fun encryptVaultItem(data: ByteArray, vaultKey: ByteArray): EncryptedBlob {
        val iv = ByteArray(GCM_IV_LENGTH).also { secureRandom.nextBytes(it) }
        
        val cipher = Cipher.getInstance(AES_MODE)
        val keySpec = SecretKeySpec(vaultKey, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
        val ciphertext = cipher.doFinal(data)
        
        return EncryptedBlob(
            iv = iv,
            ciphertext = ciphertext
        )
    }

    fun decryptVaultItem(blob: EncryptedBlob, vaultKey: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        val keySpec = SecretKeySpec(vaultKey, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, blob.iv)
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        return cipher.doFinal(blob.ciphertext)
    }

    fun encryptForExport(data: ByteArray, password: String): ExportedEncryptedBlob {
        val salt = ByteArray(32).also { secureRandom.nextBytes(it) }
        val iv = ByteArray(GCM_IV_LENGTH).also { secureRandom.nextBytes(it) }
        
        val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withMemoryAsKB(ARGON2_MEMORY)
            .withIterations(ARGON2_ITERATIONS)
            .withParallelism(ARGON2_PARALLELISM)
            .withSalt(salt)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(params)

        val exportKey = ByteArray(KEY_LENGTH / 8)
        generator.generateBytes(password.toCharArray(), exportKey)

        val cipher = Cipher.getInstance(AES_MODE)
        val keySpec = SecretKeySpec(exportKey, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
        val ciphertext = cipher.doFinal(data)
        
        return ExportedEncryptedBlob(
            salt = salt,
            iv = iv,
            ciphertext = ciphertext
        )
    }

    fun decryptExport(blob: ExportedEncryptedBlob, password: String): ByteArray {
        val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withMemoryAsKB(ARGON2_MEMORY)
            .withIterations(ARGON2_ITERATIONS)
            .withParallelism(ARGON2_PARALLELISM)
            .withSalt(blob.salt)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(params)

        val exportKey = ByteArray(KEY_LENGTH / 8)
        generator.generateBytes(password.toCharArray(), exportKey)

        val cipher = Cipher.getInstance(AES_MODE)
        val keySpec = SecretKeySpec(exportKey, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, blob.iv)
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        return cipher.doFinal(blob.ciphertext)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun hashAuthKey(authKey: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(authKey)
        return KotlinBase64.encode(hash)
    }

    fun generateRandomPassword(
        length: Int = 16,
        useUppercase: Boolean = true,
        useLowercase: Boolean = true,
        useDigits: Boolean = true,
        useSymbols: Boolean = true,
        excludeAmbiguous: Boolean = false
    ): String {
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*()_+-=[]{}|;:,.<>?"
        val ambiguous = "0O1lI"

        var chars = ""
        if (useUppercase) chars += uppercase
        if (useLowercase) chars += lowercase
        if (useDigits) chars += digits
        if (useSymbols) chars += symbols

        if (excludeAmbiguous) {
            chars = chars.filter { it !in ambiguous }
        }

        return (1..length).map { chars[secureRandom.nextInt(chars.length)] }.joinToString("")
    }

    fun getOrCreateKeystoreKey(): SecretKey {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_LENGTH)
                .setUserAuthenticationRequired(false)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    fun encryptWithKeystore(data: ByteArray): ByteArray {
        val key = getOrCreateKeystoreKey()
        val iv = ByteArray(GCM_IV_LENGTH).also { secureRandom.nextBytes(it) }
        
        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
        
        return iv + cipher.doFinal(data)
    }

    fun decryptWithKeystore(encryptedData: ByteArray): ByteArray {
        val key = getOrCreateKeystoreKey()
        val iv = encryptedData.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = encryptedData.copyOfRange(GCM_IV_LENGTH, encryptedData.size)
        
        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)
        
        return cipher.doFinal(ciphertext)
    }
}

data class DerivedKeys(
    val masterKey: ByteArray,
    val authKey: ByteArray,
    val vaultKey: ByteArray,
    val salt: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivedKeys

        if (!masterKey.contentEquals(other.masterKey)) return false
        if (!authKey.contentEquals(other.authKey)) return false
        if (!vaultKey.contentEquals(other.vaultKey)) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = masterKey.contentHashCode()
        result = 31 * result + authKey.contentHashCode()
        result = 31 * result + vaultKey.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }
}

data class EncryptedBlob(
    val iv: ByteArray,
    val ciphertext: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedBlob

        if (!iv.contentEquals(other.iv)) return false
        if (!ciphertext.contentEquals(other.ciphertext)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        return result
    }
}

data class ExportedEncryptedBlob(
    val salt: ByteArray,
    val iv: ByteArray,
    val ciphertext: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExportedEncryptedBlob

        if (!salt.contentEquals(other.salt)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!ciphertext.contentEquals(other.ciphertext)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        return result
    }
}
