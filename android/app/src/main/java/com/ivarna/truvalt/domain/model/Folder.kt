package com.ivarna.truvalt.domain.model

import java.util.UUID

data class Folder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String? = null,
    val parentId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)

data class VaultItemTag(
    val itemId: String,
    val tagId: String
)
