package com.ivarna.truvalt.data.remote.dto

data class BackendAuthRequest(
    val email: String,
    val password: String,
    val auth_key_hash: String? = null
)

data class BackendGoogleLoginRequest(
    val id_token: String,
    val auth_key_hash: String? = null
)

data class BackendUserDto(
    val id: String,
    val email: String,
    val providers: List<String> = emptyList(),
    val auth_key_hash_configured: Boolean = false,
    val created_at: Long = 0L,
    val updated_at: Long = 0L,
    val last_login_at: Long? = null,
    val email_verified: Boolean = false
)

data class BackendAuthResponse(
    val user: BackendUserDto,
    val token: String,
    val refresh_token: String? = null,
    val expires_in: Int? = null
)

data class BackendFolderRequest(
    val id: String? = null,
    val name: String,
    val icon: String? = null,
    val parent_id: String? = null
)

data class BackendFolderDto(
    val id: String,
    val user_id: String,
    val name: String,
    val icon: String? = null,
    val parent_id: String? = null,
    val updated_at: Long
)

data class BackendTagRequest(
    val id: String? = null,
    val name: String
)

data class BackendTagDto(
    val id: String,
    val user_id: String,
    val name: String
)

data class BackendVaultItemPayload(
    val id: String,
    val type: String,
    val name: String,
    val encrypted_data: String,
    val updated_at: Long,
    val created_at: Long? = null,
    val folder_id: String? = null,
    val favorite: Boolean = false,
    val deleted_at: Long? = null
)

data class BackendVaultItemDto(
    val id: String,
    val user_id: String,
    val type: String,
    val name: String,
    val folder_id: String? = null,
    val encrypted_data: String,
    val favorite: Boolean = false,
    val created_at: Long,
    val updated_at: Long,
    val deleted_at: Long? = null
)

data class BackendSyncRequest(
    val items: List<BackendVaultItemPayload>
)

data class BackendSyncResponse(
    val synced: List<BackendVaultItemDto>,
    val conflicts: List<BackendVaultItemDto>
)

data class BackendMessageResponse(
    val message: String
)

data class BackendErrorResponse(
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)
