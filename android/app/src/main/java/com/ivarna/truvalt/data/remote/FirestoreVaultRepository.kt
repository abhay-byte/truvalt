package com.ivarna.truvalt.data.remote

import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Direct Firestore data source — replaces the Laravel-relayed API calls.
 *
 * Firestore layout (mirrors the Laravel TruvaltFirestoreRepository):
 *   users/{uid}                       ← user profile
 *   users/{uid}/vault_items/{itemId}  ← encrypted vault items
 *   users/{uid}/folders/{folderId}    ← folders
 *   users/{uid}/tags/{tagId}          ← tags
 */
@Singleton
class FirestoreVaultRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    // ── User profile ──────────────────────────────────────────────────────────

    suspend fun upsertUserProfile(uid: String, email: String, provider: String) {
        val ref = firestore.collection("users").document(uid)
        val existing = ref.get().await()
        val now = System.currentTimeMillis() / 1000L

        val data = mutableMapOf<String, Any>(
            "id" to uid,
            "email" to email,
            "providers" to listOf(provider),
            "updated_at" to now,
            "last_login_at" to now,
        )
        if (!existing.exists()) {
            data["created_at"] = now
        }
        ref.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    // ── Vault items ───────────────────────────────────────────────────────────

    suspend fun getVaultItems(uid: String, updatedAfterSeconds: Long? = null): List<Map<String, Any?>> {
        var query = firestore.collection("users").document(uid)
            .collection("vault_items")
            .whereEqualTo("deleted_at", null)

        if (updatedAfterSeconds != null) {
            query = query.whereGreaterThan("updated_at", updatedAfterSeconds)
        }

        return query.get().await().documents.mapNotNull { it.data?.let { d -> d + mapOf("id" to it.id) } }
    }

    suspend fun getTrashedItems(uid: String): List<Map<String, Any?>> {
        val snapshot = firestore.collection("users").document(uid)
            .collection("vault_items")
            .get().await()
        return snapshot.documents
            .mapNotNull { it.data?.let { d -> d + mapOf("id" to it.id) } }
            .filter { it["deleted_at"] != null }
    }

    suspend fun getVaultItem(uid: String, itemId: String): Map<String, Any?>? {
        val doc = firestore.collection("users").document(uid)
            .collection("vault_items").document(itemId)
            .get().await()
        return if (doc.exists()) doc.data?.plus(mapOf("id" to doc.id)) else null
    }

    suspend fun saveVaultItem(uid: String, item: Map<String, Any?>) {
        val itemId = item["id"] as? String
            ?: error("vault item must have an id")
        val ref = firestore.collection("users").document(uid)
            .collection("vault_items").document(itemId)
        val existing = ref.get().await()

        val now = System.currentTimeMillis() / 1000L
        val data = mutableMapOf<String, Any?>()
        data.putAll(item)
        data["user_id"] = uid
        data["updated_at"] = item["updated_at"] ?: now
        if (!existing.exists()) {
            data["created_at"] = item["created_at"] ?: now
        }
        ref.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    /**
     * Soft-delete: sets deleted_at to current timestamp.
     * Returns updated item map.
     */
    suspend fun softDeleteVaultItem(uid: String, itemId: String): Map<String, Any?> {
        val now = System.currentTimeMillis() / 1000L
        val ref = firestore.collection("users").document(uid)
            .collection("vault_items").document(itemId)
        ref.update(mapOf("deleted_at" to now, "updated_at" to now)).await()
        return getVaultItem(uid, itemId)!!
    }

    /**
     * Restore from trash.
     */
    suspend fun restoreVaultItem(uid: String, itemId: String): Map<String, Any?> {
        val now = System.currentTimeMillis() / 1000L
        val ref = firestore.collection("users").document(uid)
            .collection("vault_items").document(itemId)
        ref.update(mapOf("deleted_at" to null, "updated_at" to now)).await()
        return getVaultItem(uid, itemId)!!
    }

    /**
     * Batch sync (last-write-wins). Returns synced and conflicting items.
     */
    suspend fun syncVaultItems(
        uid: String,
        items: List<Map<String, Any?>>,
    ): Pair<List<Map<String, Any?>>, List<Map<String, Any?>>> {
        val synced = mutableListOf<Map<String, Any?>>()
        val conflicts = mutableListOf<Map<String, Any?>>()

        for (item in items) {
            val itemId = item["id"] as? String ?: continue
            val incoming = (item["updated_at"] as? Long) ?: 0L
            val existing = getVaultItem(uid, itemId)

            if (existing != null) {
                val serverUpdatedAt = when (val v = existing["updated_at"]) {
                    is Long -> v
                    is Number -> v.toLong()
                    else -> 0L
                }
                if (serverUpdatedAt > incoming) {
                    conflicts.add(existing)
                    continue
                }
            }
            saveVaultItem(uid, item)
            synced.add(getVaultItem(uid, itemId)!!)
        }

        return synced to conflicts
    }

    // ── Folders ───────────────────────────────────────────────────────────────
    suspend fun getFolders(uid: String, updatedAfterSeconds: Long? = null): List<Map<String, Any?>> {
        var query = firestore.collection("users").document(uid)
            .collection("folders")
            .whereEqualTo("deleted_at", null)

        if (updatedAfterSeconds != null) {
            query = query.whereGreaterThan("updated_at", updatedAfterSeconds)
        }

        return query.get().await().documents.mapNotNull { it.data?.let { d -> d + mapOf("id" to it.id) } }
    }

    suspend fun getTrashedFolders(uid: String): List<Map<String, Any?>> {
        val snapshot = firestore.collection("users").document(uid)
            .collection("folders")
            .get().await()
        return snapshot.documents
            .mapNotNull { it.data?.let { d -> d + mapOf("id" to it.id) } }
            .filter { it["deleted_at"] != null }
    }

    suspend fun getFolder(uid: String, folderId: String): Map<String, Any?>? {
        val doc = firestore.collection("users").document(uid)
            .collection("folders").document(folderId)
            .get().await()
        return if (doc.exists()) doc.data?.plus(mapOf("id" to doc.id)) else null
    }

    suspend fun saveFolder(uid: String, folder: Map<String, Any?>) {
        val folderId = folder["id"] as? String
            ?: error("folder must have an id")
        val ref = firestore.collection("users").document(uid)
            .collection("folders").document(folderId)
        val existing = ref.get().await()
        val now = System.currentTimeMillis() / 1000L
        val data = mutableMapOf<String, Any?>()
        data.putAll(folder)
        data["user_id"] = uid
        data["updated_at"] = folder["updated_at"] ?: now
        if (!existing.exists()) {
            data["created_at"] = folder["created_at"] ?: now
        }
        ref.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    /**
     * Soft-delete folder: sets deleted_at to current timestamp.
     */
    suspend fun softDeleteFolder(uid: String, folderId: String) {
        val now = System.currentTimeMillis() / 1000L
        val ref = firestore.collection("users").document(uid)
            .collection("folders").document(folderId)
        ref.update(mapOf("deleted_at" to now, "updated_at" to now)).await()
    }

    suspend fun deleteFolder(uid: String, folderId: String) {
        firestore.collection("users").document(uid)
            .collection("folders").document(folderId)
            .delete().await()
    }

    // ── Tags ──────────────────────────────────────────────────────────────────
    suspend fun getTags(uid: String, updatedAfterSeconds: Long? = null): List<Map<String, Any?>> {
        var query = firestore.collection("users").document(uid)
            .collection("tags")
            .whereEqualTo("deleted_at", null)

        if (updatedAfterSeconds != null) {
            query = query.whereGreaterThan("updated_at", updatedAfterSeconds)
        }

        return query.get().await().documents.mapNotNull { it.data?.let { d -> d + mapOf("id" to it.id) } }
    }

    suspend fun getTrashedTags(uid: String): List<Map<String, Any?>> {
        val snapshot = firestore.collection("users").document(uid)
            .collection("tags")
            .get().await()
        return snapshot.documents
            .mapNotNull { it.data?.let { d -> d + mapOf("id" to it.id) } }
            .filter { it["deleted_at"] != null }
    }

    suspend fun saveTag(uid: String, tag: Map<String, Any?>) {
        val tagId = tag["id"] as? String
            ?: error("tag must have an id")
        val ref = firestore.collection("users").document(uid)
            .collection("tags").document(tagId)
        val existing = ref.get().await()
        val now = System.currentTimeMillis() / 1000L
        val data = mutableMapOf<String, Any?>()
        data.putAll(tag)
        data["user_id"] = uid
        data["updated_at"] = tag["updated_at"] ?: now
        if (!existing.exists()) {
            data["created_at"] = tag["created_at"] ?: now
        }
        ref.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    /**
     * Soft-delete tag: sets deleted_at to current timestamp.
     */
    suspend fun softDeleteTag(uid: String, tagId: String) {
        val now = System.currentTimeMillis() / 1000L
        val ref = firestore.collection("users").document(uid)
            .collection("tags").document(tagId)
        ref.update(mapOf("deleted_at" to now, "updated_at" to now)).await()
    }

    suspend fun deleteTag(uid: String, tagId: String) {
        firestore.collection("users").document(uid)
            .collection("tags").document(tagId)
            .delete().await()
    }

    /**
     * Hard-delete ALL Firestore data for a user account.
     * Deletes every vault_item, folder, and tag document, then the user profile root document.
     * Called during account deletion — no Laravel backend needed.
     */
    suspend fun deleteAllUserData(uid: String) {
        val userRef = firestore.collection("users").document(uid)

        // Delete all vault_items
        userRef.collection("vault_items").get().await().documents
            .forEach { it.reference.delete().await() }

        // Delete all folders
        userRef.collection("folders").get().await().documents
            .forEach { it.reference.delete().await() }

        // Delete all tags
        userRef.collection("tags").get().await().documents
            .forEach { it.reference.delete().await() }

        // Delete the user profile document itself
        userRef.delete().await()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Encode raw bytes to base64 string for Firestore storage */
    fun encodeData(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)

    /** Decode base64 string from Firestore back to raw bytes */
    fun decodeData(encoded: String): ByteArray =
        Base64.decode(encoded, Base64.DEFAULT)
}
