package com.ivarna.truvalt.core.pin

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinHasher @Inject constructor() {
    
    companion object {
        private const val SALT_LENGTH = 16
        private const val HASH_LENGTH = 32
        private const val ITERATIONS = 2
        private const val MEMORY_KB = 16 * 1024
        private const val PARALLELISM = 2
    }
    
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }
    
    fun hashPin(pin: String, salt: ByteArray): String {
        val builder = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withSalt(salt)
            .withIterations(ITERATIONS)
            .withMemoryAsKB(MEMORY_KB)
            .withParallelism(PARALLELISM)
        
        val generator = Argon2BytesGenerator()
        generator.init(builder.build())
        
        val hash = ByteArray(HASH_LENGTH)
        generator.generateBytes(pin.toByteArray(Charsets.UTF_8), hash)
        
        return Base64.getEncoder().encodeToString(hash)
    }
    
    fun verifyPin(pin: String, salt: ByteArray, storedHash: String): Boolean {
        val computed = hashPin(pin, salt)
        return MessageDigest.isEqual(
            computed.toByteArray(Charsets.UTF_8),
            storedHash.toByteArray(Charsets.UTF_8)
        )
    }
}
