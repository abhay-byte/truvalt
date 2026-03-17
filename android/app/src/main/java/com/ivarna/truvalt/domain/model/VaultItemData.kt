package com.ivarna.truvalt.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomField(
    val name: String,
    val value: String,
    val isHidden: Boolean = false
)

@Serializable
data class LoginItemData(
    val url: String = "",
    val username: String = "",
    val password: String = "",
    val totpSeed: String? = null,
    val notes: String = "",
    val customFields: List<CustomField> = emptyList()
)

@Serializable
data class PasskeyItemData(
    val credentialId: String = "",
    val publicKeyJson: String = "",
    val rpId: String = "",
    val username: String = "",
    val notes: String = ""
)

@Serializable
data class PassphraseItemData(
    val passphrase: String = "",
    val wordCount: Int = 4,
    val notes: String = ""
)

@Serializable
data class SecureNoteItemData(
    val content: String = ""
)

@Serializable
data class SecurityCodeItemData(
    val code: String = "",
    val codeType: String = "",
    val issuer: String = "",
    val notes: String = ""
)

@Serializable
data class CreditCardItemData(
    val cardNumber: String = "",
    val cardholderName: String = "",
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val cvv: String = "",
    val notes: String = ""
)

@Serializable
data class IdentityItemData(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val notes: String = ""
)

@Serializable
data class CustomItemData(
    val fields: List<CustomField> = emptyList(),
    val notes: String = ""
)
