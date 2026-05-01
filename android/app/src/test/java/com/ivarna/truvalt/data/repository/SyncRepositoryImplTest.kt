package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.FolderEntity
import com.ivarna.truvalt.data.local.entity.TagEntity
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.FirebaseSessionProvider
import com.ivarna.truvalt.data.remote.FirestoreVaultRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var preferences: TruvaltPreferences
    private lateinit var vaultItemDao: VaultItemDao
    private lateinit var folderDao: FolderDao
    private lateinit var tagDao: TagDao
    private lateinit var firestoreRepository: FirestoreVaultRepository
    private lateinit var firebaseSessionProvider: FirebaseSessionProvider
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var activeNetwork: Network
    private lateinit var networkCapabilities: NetworkCapabilities

    private val testUid = "test-user-uid"

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        preferences = mockk(relaxed = true)
        vaultItemDao = mockk(relaxed = true)
        folderDao = mockk(relaxed = true)
        tagDao = mockk(relaxed = true)
        firestoreRepository = mockk(relaxed = true)
        firebaseSessionProvider = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        activeNetwork = mockk(relaxed = true)
        networkCapabilities = mockk(relaxed = true)

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns activeNetwork
        every { connectivityManager.getNetworkCapabilities(activeNetwork) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true

        every { firebaseSessionProvider.currentUserUid() } returns testUid
        every { preferences.lastSyncTime } returns flowOf(0L)
        coEvery { preferences.setLastSyncTime(any()) } just Runs
    }

    private fun createRepository(): SyncRepositoryImpl = SyncRepositoryImpl(
        context = context,
        preferences = preferences,
        vaultItemDao = vaultItemDao,
        folderDao = folderDao,
        tagDao = tagDao,
        firebaseSessionProvider = firebaseSessionProvider,
        firestoreRepository = firestoreRepository
    )

    // ── Offline / Local-only mode tests ──────────────────────────────────────

    @Test
    fun `sync fails fast when in local-only mode`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns true

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Local-only mode", result.exceptionOrNull()?.message)
    }

    @Test
    fun `sync fails fast when no internet connection`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("No internet connection", result.exceptionOrNull()?.message)
    }

    @Test
    fun `sync fails fast when firebase user is missing`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        every { firebaseSessionProvider.currentUserUid() } returns null

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Not signed in to Firebase", result.exceptionOrNull()?.message)
    }

    @Test
    fun `isOnline returns false when no active network`() = runTest {
        every { connectivityManager.activeNetwork } returns null

        val repository = createRepository()
        assertFalse(repository.isOnline())
    }

    @Test
    fun `isOnline returns false when no network capabilities`() = runTest {
        every { connectivityManager.getNetworkCapabilities(activeNetwork) } returns null

        val repository = createRepository()
        assertFalse(repository.isOnline())
    }

    @Test
    fun `isOnline returns true when internet and validated capabilities present`() = runTest {
        val repository = createRepository()
        assertTrue(repository.isOnline())
    }

    @Test
    fun `isLocalOnly delegates to preferences`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns true
        val repository = createRepository()
        assertTrue(repository.isLocalOnly())
    }

    @Test
    fun `setLocalOnly delegates to preferences`() = runTest {
        coEvery { preferences.setLocalOnly(true) } just Runs
        val repository = createRepository()
        repository.setLocalOnly(true)
        coVerify { preferences.setLocalOnly(true) }
    }

    @Test
    fun `getLastSyncTime delegates to preferences`() = runTest {
        every { preferences.lastSyncTime } returns flowOf(12345L)
        val repository = createRepository()
        assertEquals(12345L, repository.getLastSyncTime())
    }

    // ── Empty sync tests ─────────────────────────────────────────────────────

    @Test
    fun `sync succeeds when no pending local changes and no remote changes`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_DELETE") } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { preferences.setLastSyncTime(any()) }
    }

    // ── Push vault items tests ───────────────────────────────────────────────

    @Test
    fun `sync pushes pending vault items to Firestore`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        val pendingItem = VaultItemEntity(
            id = "item-1",
            type = "login",
            name = "GitHub",
            folderId = null,
            encryptedData = "encrypted".toByteArray(),
            favorite = false,
            createdAt = 1000L,
            updatedAt = 2000L,
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD") } returns listOf(pendingItem)
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_DELETE") } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.syncVaultItems(testUid, any()) } returns (listOf<Map<String, Any?>>() to emptyList())
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { firestoreRepository.syncVaultItems(testUid, any()) }
    }

    @Test
    fun `sync handles pending vault item deletions`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        val deleteItem = VaultItemEntity(
            id = "item-2",
            type = "login",
            name = "GitHub",
            folderId = null,
            encryptedData = "encrypted".toByteArray(),
            favorite = false,
            createdAt = 1000L,
            updatedAt = 3000L,
            deletedAt = 3000L,
            syncStatus = "PENDING_DELETE"
        )
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_DELETE") } returns listOf(deleteItem)
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.syncVaultItems(testUid, any()) } returns (listOf<Map<String, Any?>>() to emptyList())
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { firestoreRepository.syncVaultItems(testUid, any()) }
    }

    // ── Push folders tests ───────────────────────────────────────────────────

    @Test
    fun `sync pushes pending folders to Firestore with conflict resolution`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        val pendingFolder = FolderEntity(
            id = "folder-1",
            name = "Work",
            icon = "briefcase",
            parentId = null,
            createdAt = 1000L,
            updatedAt = 2000L,
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns listOf(pendingFolder)
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.getFolder(testUid, "folder-1") } returns null
        coEvery { firestoreRepository.saveFolder(testUid, any()) } just Runs
        coEvery { folderDao.updateSyncStatus("folder-1", "SYNCED") } just Runs
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { firestoreRepository.saveFolder(testUid, any()) }
        coVerify { folderDao.updateSyncStatus("folder-1", "SYNCED") }
    }

    @Test
    fun `sync skips folder push when server version is newer`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        val pendingFolder = FolderEntity(
            id = "folder-1",
            name = "Work",
            icon = "briefcase",
            parentId = null,
            createdAt = 1000L,
            updatedAt = 2000L,
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns listOf(pendingFolder)
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        // Server has newer version (3000 > 2000)
        coEvery { firestoreRepository.getFolder(testUid, "folder-1") } returns mapOf(
            "id" to "folder-1",
            "updated_at" to 3000L
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { firestoreRepository.saveFolder(testUid, any()) }
    }

    // ── Push tags tests ──────────────────────────────────────────────────────

    @Test
    fun `sync pushes pending tags to Firestore`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        val pendingTag = TagEntity(
            id = "tag-1",
            name = "important",
            createdAt = 1000L,
            updatedAt = 2000L,
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns listOf(pendingTag)
        coEvery { firestoreRepository.getTags(testUid) } returns emptyList()
        coEvery { firestoreRepository.saveTag(testUid, any()) } just Runs
        coEvery { tagDao.updateSyncStatus("tag-1", "SYNCED") } just Runs
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { firestoreRepository.saveTag(testUid, any()) }
        coVerify { tagDao.updateSyncStatus("tag-1", "SYNCED") }
    }

    // ── Pull remote state tests ──────────────────────────────────────────────

    @Test
    fun `sync pulls remote vault items and inserts into Room`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()

        val remoteItem = mapOf<String, Any?>(
            "id" to "remote-item-1",
            "type" to "login",
            "name" to "Remote GitHub",
            "folder_id" to null,
            "encrypted_data" to "c29tZS1lbmNyeXB0ZWQtZGF0YQ==",
            "favorite" to true,
            "created_at" to 1000L,
            "updated_at" to 2000L,
            "deleted_at" to null
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns listOf(remoteItem)
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { vaultItemDao.insertItems(any()) }
        coVerify { vaultItemDao.updateSyncStatus("remote-item-1", "SYNCED") }
    }

    @Test
    fun `sync pulls remote folders and inserts into Room`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()

        val remoteFolder = mapOf<String, Any?>(
            "id" to "remote-folder-1",
            "name" to "Remote Work",
            "icon" to "work",
            "parent_id" to null,
            "created_at" to 1000L,
            "updated_at" to 2000L,
            "deleted_at" to null
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns listOf(remoteFolder)
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { folderDao.insertFolders(any()) }
        coVerify { folderDao.updateSyncStatus("remote-folder-1", "SYNCED") }
    }

    @Test
    fun `sync pulls remote tags and inserts into Room`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()

        val remoteTag = mapOf<String, Any?>(
            "id" to "remote-tag-1",
            "name" to "Remote Important",
            "created_at" to 1000L,
            "updated_at" to 2000L,
            "deleted_at" to null
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns listOf(remoteTag)
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { tagDao.insertTags(any()) }
        coVerify { tagDao.updateSyncStatus("remote-tag-1", "SYNCED") }
    }

    @Test
    fun `sync pulls trashed items from remote`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()

        val trashedItem = mapOf<String, Any?>(
            "id" to "trashed-item-1",
            "type" to "login",
            "name" to "Old GitHub",
            "encrypted_data" to "c29tZS1lbmNyeXB0ZWQtZGF0YQ==",
            "created_at" to 1000L,
            "updated_at" to 2000L,
            "deleted_at" to 2000L
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns listOf(trashedItem)
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { vaultItemDao.insertItems(any()) }
    }

    // ── Conflict resolution tests ────────────────────────────────────────────

    @Test
    fun `sync resolves vault item conflicts with last-write-wins`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        val pendingItem = VaultItemEntity(
            id = "item-1",
            type = "login",
            name = "Local GitHub",
            folderId = null,
            encryptedData = "local-encrypted".toByteArray(),
            favorite = false,
            createdAt = 1000L,
            updatedAt = 5000L, // Local is newer
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD") } returns listOf(pendingItem)
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_DELETE") } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()

        val serverSyncedItem = mapOf<String, Any?>(
            "id" to "item-1",
            "type" to "login",
            "name" to "Synced GitHub",
            "encrypted_data" to "c29tZS1lbmNyeXB0ZWQtZGF0YQ==",
            "updated_at" to 5000L
        )
        coEvery { firestoreRepository.syncVaultItems(testUid, any()) } returns (
            listOf(serverSyncedItem) to emptyList()
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { vaultItemDao.insertItems(any()) }
        coVerify { vaultItemDao.updateSyncStatus("item-1", "SYNCED") }
    }

    @Test
    fun `sync handles Firestore exceptions gracefully`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } throws RuntimeException("Firestore error")

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Firestore error", result.exceptionOrNull()?.message)
    }

    // ── Delta sync tests ─────────────────────────────────────────────────────

    @Test
    fun `sync uses lastSyncTime for delta queries`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { preferences.lastSyncTime } returns flowOf(10000L)
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.getVaultItems(testUid, 10L) } returns emptyList() // 10000ms / 1000 = 10s
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, 10L) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, 10L) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { firestoreRepository.getVaultItems(testUid, 10L) }
        coVerify { firestoreRepository.getFolders(testUid, 10L) }
        coVerify { firestoreRepository.getTags(testUid, 10L) }
    }

    // ── Timestamp conversion tests (indirect via sync) ───────────────────────

    @Test
    fun `sync converts timestamps correctly between seconds and milliseconds`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns false
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()

        val remoteItem = mapOf<String, Any?>(
            "id" to "time-test",
            "type" to "note",
            "name" to "Time Test",
            "encrypted_data" to "dGVzdA==",
            "created_at" to 5L,  // 5 seconds = 5000ms
            "updated_at" to 10L, // 10 seconds = 10000ms
            "deleted_at" to null
        )
        coEvery { firestoreRepository.getVaultItems(testUid, any()) } returns listOf(remoteItem)
        coEvery { firestoreRepository.getTrashedItems(testUid) } returns emptyList()
        coEvery { firestoreRepository.getFolders(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(testUid) } returns emptyList()
        coEvery { firestoreRepository.getTags(testUid, any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(testUid) } returns emptyList()

        val repository = createRepository()
        repository.sync()

        coVerify { vaultItemDao.insertItems(any()) }
    }
}
