package com.ivarna.truvalt.domain.model

sealed class VaultItemType(val id: String, val displayName: String) {
    data object Login : VaultItemType("login", "Login")
    data object Passkey : VaultItemType("passkey", "Passkey")
    data object Passphrase : VaultItemType("passphrase", "Passphrase")
    data object SecureNote : VaultItemType("secure_note", "Secure Note")
    data object SecurityCode : VaultItemType("security_code", "Security/Recovery Code")
    data object CreditCard : VaultItemType("credit_card", "Credit Card")
    data object Identity : VaultItemType("identity", "Identity")
    data object Custom : VaultItemType("custom", "Custom")

    companion object {
        fun fromId(id: String): VaultItemType = when (id) {
            "login" -> Login
            "passkey" -> Passkey
            "passphrase" -> Passphrase
            "secure_note" -> SecureNote
            "security_code" -> SecurityCode
            "credit_card" -> CreditCard
            "identity" -> Identity
            else -> Custom
        }

        fun getAllTypes() = listOf(
            Login, Passkey, Passphrase, SecureNote,
            SecurityCode, CreditCard, Identity, Custom
        )
    }
}
