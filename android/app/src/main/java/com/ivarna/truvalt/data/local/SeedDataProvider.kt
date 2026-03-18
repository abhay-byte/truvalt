package com.ivarna.truvalt.data.local

import com.ivarna.truvalt.domain.model.VaultItemType
import org.json.JSONObject
import java.util.UUID

data class SeedItem(
    val id: String = UUID.randomUUID().toString(),
    val type: VaultItemType,
    val name: String,
    val data: Map<String, Any>,
    val favorite: Boolean = false
)

object SeedDataProvider {
    
    fun getSeedItems(): List<SeedItem> = listOf(
        // LOGINS
        SeedItem(
            type = VaultItemType.Login,
            name = "GitHub",
            data = mapOf(
                "url" to "https://github.com",
                "username" to "dev@truvalt.app",
                "password" to "Tr!v4lt#Dev2025",
                "notes" to "Development account"
            ),
            favorite = true
        ),
        SeedItem(
            type = VaultItemType.Login,
            name = "Gmail",
            data = mapOf(
                "url" to "https://gmail.com",
                "username" to "personal@gmail.com",
                "password" to "G0ogl3\$P4ss!",
                "notes" to "Personal email"
            )
        ),
        
        // PASSKEYS
        SeedItem(
            type = VaultItemType.Passkey,
            name = "iCloud",
            data = mapOf(
                "service" to "iCloud",
                "username" to "user@icloud.com",
                "rp_id" to "apple.com",
                "notes" to "Apple ID passkey"
            )
        ),
        SeedItem(
            type = VaultItemType.Passkey,
            name = "Microsoft",
            data = mapOf(
                "service" to "Microsoft",
                "username" to "user@outlook.com",
                "rp_id" to "microsoft.com",
                "notes" to "Microsoft account passkey"
            )
        ),
        
        // PASSPHRASES
        SeedItem(
            type = VaultItemType.Passphrase,
            name = "Bitwarden Recovery",
            data = mapOf(
                "phrase" to "correct-horse-battery-staple-lamp",
                "context" to "Backup vault recovery phrase"
            )
        ),
        SeedItem(
            type = VaultItemType.Passphrase,
            name = "SSH Key",
            data = mapOf(
                "phrase" to "tangerine-falcon-seven-moon-drift",
                "context" to "Home server SSH passphrase"
            )
        ),
        
        // SECURE NOTES
        SeedItem(
            type = VaultItemType.SecureNote,
            name = "Wi-Fi Passwords",
            data = mapOf(
                "content" to """
                    Home: HomeNet5G / Pass: CoffeeBeans2024
                    Office: OfficeWifi / Pass: Office@2025
                """.trimIndent()
            )
        ),
        SeedItem(
            type = VaultItemType.SecureNote,
            name = "Server Setup Notes",
            data = mapOf(
                "content" to """
                    Ubuntu 22.04 LTS
                    Nginx 1.24
                    PostgreSQL 16
                    SSH port: 2222
                """.trimIndent()
            )
        ),
        
        // TOTP (SECURITY_CODE type for now, will be TOTP when available)
        SeedItem(
            type = VaultItemType.SecurityCode,
            name = "GitHub 2FA",
            data = mapOf(
                "secret" to "JBSWY3DPEHPK3PXP",
                "issuer" to "GitHub",
                "account" to "dev@truvalt.app",
                "type" to "totp"
            )
        ),
        SeedItem(
            type = VaultItemType.SecurityCode,
            name = "Google 2FA",
            data = mapOf(
                "secret" to "JBSWY3DPEHPK3PXP",
                "issuer" to "Google",
                "account" to "personal@gmail.com",
                "type" to "totp"
            )
        ),
        
        // SECURITY CODES (Backup codes)
        SeedItem(
            type = VaultItemType.SecurityCode,
            name = "GitHub Backup",
            data = mapOf(
                "codes" to listOf(
                    "a1b2-c3d4", "e5f6-g7h8", "i9j0-k1l2", "m3n4-o5p6",
                    "q7r8-s9t0", "u1v2-w3x4", "y5z6-a7b8", "c9d0-e1f2"
                ),
                "type" to "backup"
            )
        ),
        SeedItem(
            type = VaultItemType.SecurityCode,
            name = "Google Backup",
            data = mapOf(
                "codes" to listOf(
                    "1234 5678", "2345 6789", "3456 7890", "4567 8901",
                    "5678 9012", "6789 0123", "7890 1234", "8901 2345"
                ),
                "type" to "backup"
            )
        ),
        
        // CREDIT CARDS
        SeedItem(
            type = VaultItemType.CreditCard,
            name = "Visa Debit",
            data = mapOf(
                "number" to "4111111111111111",
                "expiry" to "12/27",
                "cvv" to "123",
                "name" to "Dev User",
                "notes" to "Primary debit card"
            )
        ),
        SeedItem(
            type = VaultItemType.CreditCard,
            name = "Mastercard",
            data = mapOf(
                "number" to "5500005555555559",
                "expiry" to "06/26",
                "cvv" to "456",
                "name" to "Dev User",
                "notes" to "Backup credit card"
            )
        ),
        
        // IDENTITY
        SeedItem(
            type = VaultItemType.Identity,
            name = "Personal",
            data = mapOf(
                "name" to "Dev User",
                "dob" to "1990-01-15",
                "email" to "dev@truvalt.app",
                "phone" to "+1-555-0100",
                "notes" to "Personal identity"
            )
        ),
        SeedItem(
            type = VaultItemType.Identity,
            name = "Work",
            data = mapOf(
                "name" to "Dev User",
                "company" to "Truvalt Inc.",
                "email" to "work@truvalt.app",
                "phone" to "+1-555-0200",
                "notes" to "Work identity"
            )
        )
    )
    
    fun toJsonString(data: Map<String, Any>): String {
        val json = JSONObject()
        data.forEach { (key, value) ->
            when (value) {
                is List<*> -> json.put(key, org.json.JSONArray(value))
                else -> json.put(key, value)
            }
        }
        return json.toString()
    }
}
