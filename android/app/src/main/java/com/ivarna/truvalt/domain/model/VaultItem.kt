package com.ivarna.truvalt.domain.model

import java.util.UUID

data class VaultItem(
    val id: String = UUID.randomUUID().toString(),
    val type: VaultItemType,
    val name: String,
    val folderId: String? = null,
    val encryptedData: ByteArray,
    val favorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VaultItem

        if (id != other.id) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (folderId != other.folderId) return false
        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (favorite != other.favorite) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (deletedAt != other.deletedAt) return false
        if (syncStatus != other.syncStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (folderId?.hashCode() ?: 0)
        result = 31 * result + encryptedData.contentHashCode()
        result = 31 * result + favorite.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + (deletedAt?.hashCode() ?: 0)
        result = 31 * result + syncStatus.hashCode()
        return result
    }
}

enum class VaultItemType {
    LOGIN,
    PASSKEY,
    PASSPHRASE,
    NOTE,
    SECURITY_CODE,
    CARD,
    IDENTITY,
    CUSTOM
}

enum class SyncStatus {
    SYNCED,
    PENDING_UPLOAD,
    PENDING_DELETE
}
