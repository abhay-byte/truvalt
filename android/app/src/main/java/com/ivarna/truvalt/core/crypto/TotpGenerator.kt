package com.ivarna.truvalt.core.crypto

import org.apache.commons.codec.binary.Base32
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TotpGenerator {
    
    fun generate(base32Secret: String, period: Long = 30L): String {
        val key = Base32().decode(base32Secret.uppercase().replace(" ", ""))
        val timeStep = System.currentTimeMillis() / 1000L / period
        return generateHotp(key, timeStep)
    }
    
    private fun generateHotp(key: ByteArray, counter: Long): String {
        val data = ByteBuffer.allocate(8).putLong(counter).array()
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "RAW"))
        val hash = mac.doFinal(data)
        val offset = hash.last().toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                     ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                     ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                     (hash[offset + 3].toInt() and 0xFF)
        return (binary % 1_000_000).toString().padStart(6, '0')
    }
}
