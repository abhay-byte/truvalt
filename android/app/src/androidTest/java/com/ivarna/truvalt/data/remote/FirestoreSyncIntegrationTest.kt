package com.ivarna.truvalt.data.remote

import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

/**
 * Integration tests for Firebase sync operations.
 *
 * These tests create real Firebase Auth accounts and write/read actual Firestore data.
 * Each test cleans up its test data in a finally block.
 */
class FirestoreSyncIntegrationTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var repo: FirestoreVaultRepository
    private var testUid: String? = null

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        auth = FirebaseAuth.getInstance()
        repo = FirestoreVaultRepository(FirebaseFirestore.getInstance())
    }

    @After
    fun tearDown() = runBlocking {
        // Clean up test data
        testUid?.let { uid ->
            runCatching { repo.deleteAllUserData(uid) }
            auth.currentUser?.delete()?.await()
        }
        auth.signOut()
    }

    private suspend fun createTestUser(): String {
        auth.signOut()
        val email = "sync-test-${UUID.randomUUID()}@example.com"
        val password = "Test1234!${UUID.randomUUID().toString().take(8)}"
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return requireNotNull(result.user?.uid) { "Firebase auth did not return a user" }
    }

    // ── Vault item sync tests ────────────────────────────────────────────────

    @Test
    fun syncVaultItems_performsLastWriteWins() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val itemId = UUID.randomUUID().toString()
            val encodedData = repo.encodeData("test-data".toByteArray())

            // Create initial item on server
            val initial = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "login",
                "name" to "Initial",
                "encrypted_data" to encodedData,
                "updated_at" to 1000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            repo.saveVaultItem(uid, initial)

            // Sync with newer local version
            val localUpdate = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "login",
                "name" to "Updated",
                "encrypted_data" to encodedData,
                "updated_at" to 2000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            val (synced, conflicts) = repo.syncVaultItems(uid, listOf(localUpdate))

            assertTrue("Expected synced items", synced.isNotEmpty())
            assertTrue("Expected no conflicts", conflicts.isEmpty())
            assertEquals("Updated", synced[0]["name"])

            // Verify server has the updated version
            val serverItem = repo.getVaultItem(uid, itemId)
            assertNotNull(serverItem)
            assertEquals("Updated", serverItem?.get("name"))
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    @Test
    fun syncVaultItems_detectsConflictWhenServerIsNewer() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val itemId = UUID.randomUUID().toString()
            val encodedData = repo.encodeData("test-data".toByteArray())

            // Create item on server with newer timestamp
            val serverVersion = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "login",
                "name" to "Server Version",
                "encrypted_data" to encodedData,
                "updated_at" to 3000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            repo.saveVaultItem(uid, serverVersion)

            // Try to sync with older local version
            val localVersion = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "login",
                "name" to "Local Version",
                "encrypted_data" to encodedData,
                "updated_at" to 2000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            val (synced, conflicts) = repo.syncVaultItems(uid, listOf(localVersion))

            assertTrue("Expected no synced items", synced.isEmpty())
            assertTrue("Expected conflicts", conflicts.isNotEmpty())
            assertEquals("Server Version", conflicts[0]["name"])
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    @Test
    fun syncVaultItems_createsNewItemWhenNotExistsOnServer() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val itemId = UUID.randomUUID().toString()
            val encodedData = repo.encodeData("new-item-data".toByteArray())

            val newItem = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "note",
                "name" to "New Note",
                "encrypted_data" to encodedData,
                "updated_at" to 1000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            val (synced, conflicts) = repo.syncVaultItems(uid, listOf(newItem))

            assertTrue("Expected synced items", synced.isNotEmpty())
            assertTrue("Expected no conflicts", conflicts.isEmpty())

            val serverItem = repo.getVaultItem(uid, itemId)
            assertNotNull(serverItem)
            assertEquals("New Note", serverItem?.get("name"))
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── Delta sync tests ─────────────────────────────────────────────────────

    @Test
    fun getVaultItems_withUpdatedAfter_returnsOnlyNewerItems() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val encodedData = repo.encodeData("delta-test".toByteArray())

            // Create old item
            val oldItem = mapOf<String, Any?>(
                "id" to "old-item",
                "type" to "login",
                "name" to "Old",
                "encrypted_data" to encodedData,
                "updated_at" to 1000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            repo.saveVaultItem(uid, oldItem)

            // Create new item
            val newItem = mapOf<String, Any?>(
                "id" to "new-item",
                "type" to "login",
                "name" to "New",
                "encrypted_data" to encodedData,
                "updated_at" to 5000L,
                "created_at" to 5000L,
                "deleted_at" to null
            )
            repo.saveVaultItem(uid, newItem)

            // Query for items updated after 2000L
            val deltaItems = repo.getVaultItems(uid, 2000L)

            assertEquals(1, deltaItems.size)
            assertEquals("New", deltaItems[0]["name"])
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── Folder sync tests ────────────────────────────────────────────────────

    @Test
    fun folderCrud_syncsCorrectly() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val folderId = UUID.randomUUID().toString()

            // Create folder
            val folder = mapOf<String, Any?>(
                "id" to folderId,
                "name" to "Work",
                "icon" to "briefcase",
                "parent_id" to null,
                "created_at" to 1000L,
                "updated_at" to 1000L,
                "deleted_at" to null
            )
            repo.saveFolder(uid, folder)

            val created = repo.getFolder(uid, folderId)
            assertNotNull(created)
            assertEquals("Work", created?.get("name"))

            // Update folder
            val updated = mapOf<String, Any?>(
                "id" to folderId,
                "name" to "Work Updated",
                "icon" to "work",
                "parent_id" to null,
                "created_at" to 1000L,
                "updated_at" to 2000L,
                "deleted_at" to null
            )
            repo.saveFolder(uid, updated)

            val afterUpdate = repo.getFolder(uid, folderId)
            assertEquals("Work Updated", afterUpdate?.get("name"))
            assertEquals("work", afterUpdate?.get("icon"))

            // Soft delete
            repo.softDeleteFolder(uid, folderId)
            val allFolders = repo.getFolders(uid)
            assertTrue(allFolders.none { it["id"] == folderId })

            // Hard delete
            repo.deleteFolder(uid, folderId)
            assertNull(repo.getFolder(uid, folderId))
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── Tag sync tests ───────────────────────────────────────────────────────

    @Test
    fun tagCrud_syncsCorrectly() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val tagId = UUID.randomUUID().toString()

            // Create tag
            val tag = mapOf<String, Any?>(
                "id" to tagId,
                "name" to "important",
                "created_at" to 1000L,
                "updated_at" to 1000L,
                "deleted_at" to null
            )
            repo.saveTag(uid, tag)

            val created = repo.getTags(uid).firstOrNull { it["id"] == tagId }
            assertNotNull(created)
            assertEquals("important", created?.get("name"))

            // Update tag
            val updated = mapOf<String, Any?>(
                "id" to tagId,
                "name" to "very-important",
                "created_at" to 1000L,
                "updated_at" to 2000L,
                "deleted_at" to null
            )
            repo.saveTag(uid, updated)

            val afterUpdate = repo.getTags(uid).firstOrNull { it["id"] == tagId }
            assertEquals("very-important", afterUpdate?.get("name"))

            // Soft delete
            repo.softDeleteTag(uid, tagId)
            val activeTags = repo.getTags(uid)
            assertTrue(activeTags.none { it["id"] == tagId })

            // Hard delete
            repo.deleteTag(uid, tagId)
            val trashedTags = repo.getTrashedTags(uid)
            assertTrue(trashedTags.none { it["id"] == tagId })
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── User profile tests ───────────────────────────────────────────────────

    @Test
    fun upsertUserProfile_createsAndUpdatesProfile() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            // Create profile
            repo.upsertUserProfile(uid, "test@example.com", "password")

            val firestore = FirebaseFirestore.getInstance()
            val profile = firestore.collection("users").document(uid).get().await()
            assertTrue(profile.exists())
            assertEquals("test@example.com", profile.getString("email"))
            assertEquals(listOf("password"), profile.get("providers"))
            assertNotNull(profile.getLong("created_at"))

            // Update profile (login again)
            val createdAt = profile.getLong("created_at")
            repo.upsertUserProfile(uid, "test@example.com", "google.com")

            val updated = firestore.collection("users").document(uid).get().await()
            assertTrue(updated.exists())
            assertEquals(createdAt, updated.getLong("created_at")) // Created at should not change
            assertNotNull(updated.getLong("updated_at"))
            assertNotNull(updated.getLong("last_login_at"))
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── Batch sync with multiple items ───────────────────────────────────────

    @Test
    fun syncVaultItems_batchSyncsMultipleItems() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val encodedData = repo.encodeData("batch-test".toByteArray())
            val items = (1..5).map { i ->
                mapOf<String, Any?>(
                    "id" to UUID.randomUUID().toString(),
                    "type" to "login",
                    "name" to "Item $i",
                    "encrypted_data" to encodedData,
                    "updated_at" to (i * 1000L),
                    "created_at" to (i * 1000L),
                    "deleted_at" to null
                )
            }

            val (synced, conflicts) = repo.syncVaultItems(uid, items)

            assertEquals(5, synced.size)
            assertTrue(conflicts.isEmpty())

            // Verify all items exist on server
            val serverItems = repo.getVaultItems(uid)
            assertEquals(5, serverItems.size)
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── Trash and restore tests ──────────────────────────────────────────────

    @Test
    fun softDeleteVaultItem_movesToTrash() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        try {
            val itemId = UUID.randomUUID().toString()
            val encodedData = repo.encodeData("trash-test".toByteArray())
            val item = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "login",
                "name" to "To Trash",
                "encrypted_data" to encodedData,
                "updated_at" to 1000L,
                "created_at" to 1000L,
                "deleted_at" to null
            )
            repo.saveVaultItem(uid, item)

            // Soft delete
            val trashed = repo.softDeleteVaultItem(uid, itemId)
            assertNotNull(trashed["deleted_at"])

            // Should not appear in active items
            val active = repo.getVaultItems(uid)
            assertTrue(active.none { it["id"] == itemId })

            // Should appear in trashed items
            val trashedItems = repo.getTrashedItems(uid)
            assertTrue(trashedItems.any { it["id"] == itemId })

            // Restore
            val restored = repo.restoreVaultItem(uid, itemId)
            assertNull(restored["deleted_at"])

            val afterRestore = repo.getVaultItems(uid)
            assertTrue(afterRestore.any { it["id"] == itemId })
        } finally {
            testUid?.let { runCatching { repo.deleteAllUserData(it) } }
        }
    }

    // ── Delete all user data tests ───────────────────────────────────────────

    @Test
    fun deleteAllUserData_removesEverything() = runBlocking {
        testUid = createTestUser()
        val uid = testUid!!

        // Create various data
        val encodedData = repo.encodeData("cleanup-test".toByteArray())
        repo.saveVaultItem(uid, mapOf(
            "id" to "item-1",
            "type" to "login",
            "name" to "Test",
            "encrypted_data" to encodedData,
            "updated_at" to 1000L,
            "created_at" to 1000L,
            "deleted_at" to null
        ))
        repo.saveFolder(uid, mapOf(
            "id" to "folder-1",
            "name" to "Test Folder",
            "icon" to null,
            "parent_id" to null,
            "created_at" to 1000L,
            "updated_at" to 1000L,
            "deleted_at" to null
        ))
        repo.saveTag(uid, mapOf(
            "id" to "tag-1",
            "name" to "test-tag",
            "created_at" to 1000L,
            "updated_at" to 1000L,
            "deleted_at" to null
        ))
        repo.upsertUserProfile(uid, "cleanup@test.com", "password")

        // Delete everything
        repo.deleteAllUserData(uid)

        // Verify all gone
        assertTrue(repo.getVaultItems(uid).isEmpty())
        assertTrue(repo.getFolders(uid).isEmpty())
        assertTrue(repo.getTags(uid).isEmpty())

        val firestore = FirebaseFirestore.getInstance()
        val profile = firestore.collection("users").document(uid).get().await()
        assertTrue(!profile.exists())
    }
}
