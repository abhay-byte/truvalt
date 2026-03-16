package com.ivarna.truvalt.domain.model

data class LoginItem(
    val url: String = "",
    val username: String = "",
    val password: String = "",
    val notes: String = "",
    val totpSeed: String? = null,
    val customFields: List<CustomField> = emptyList()
)

data class CustomField(
    val name: String,
    val value: String,
    val isHidden: Boolean = false
)

data class SecureNote(
    val content: String
)

data class Passphrase(
    val passphrase: String,
    val wordCount: Int = 4,
    val notes: String = ""
)

data class SecurityCode(
    val code: String,
    val issuer: String = "",
    val notes: String = ""
)

data class TotpCode(
    val seed: String,
    val issuer: String = "",
    val accountName: String = ""
)
