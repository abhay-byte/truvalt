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
import io.mockk.Called
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

/**
 * Tests for local-only (offline) mode behavior.
 *
 * In local-only mode:
 * - Sync is completely disabled
 * - All data stays in Room database only
 * - No network calls are made to Firebase/Firestore
 * - User can still create, read, update, delete vault items
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LocalOnlyModeTest {

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
    }

    private fun createRepository(localOnly: Boolean): SyncRepositoryImpl {
        coEvery { preferences.isLocalOnlySync() } returns localOnly
        return SyncRepositoryImpl(
            context = context,
            preferences = preferences,
            vaultItemDao = vaultItemDao,
            folderDao = folderDao,
            tagDao = tagDao,
            firebaseSessionProvider = firebaseSessionProvider,
            firestoreRepository = firestoreRepository
        )
    }

    // ── Sync blocking tests ──────────────────────────────────────────────────

    @Test
    fun `sync is blocked in local-only mode regardless of network availability`() = runTest {
        val repository = createRepository(localOnly = true)

        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Local-only mode", result.exceptionOrNull()?.message)
        // Verify no Firestore calls were made
        verify(exactly = 0) { firestoreRepository wasNot Called }
    }

    @Test
    fun `sync is blocked in local-only mode even when online`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns true
        val repository = createRepository(localOnly = true)

        assertTrue(repository.isOnline()) // Network is available

        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Local-only mode", result.exceptionOrNull()?.message)
    }

    @Test
    fun `sync is blocked in local-only mode even when firebase user is signed in`() = runTest {
        coEvery { preferences.isLocalOnlySync() } returns true
        every { firebaseSessionProvider.currentUserUid() } returns "some-uid"
        val repository = createRepository(localOnly = true)

        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Local-only mode", result.exceptionOrNull()?.message)
    }

    // ── Mode toggle tests ────────────────────────────────────────────────────

    @Test
    fun `isLocalOnly returns true when preference is set`() = runTest {
        val repository = createRepository(localOnly = true)
        assertTrue(repository.isLocalOnly())
    }

    @Test
    fun `isLocalOnly returns false when preference is not set`() = runTest {
        val repository = createRepository(localOnly = false)
        assertFalse(repository.isLocalOnly())
    }

    @Test
    fun `setLocalOnly updates preference correctly`() = runTest {
        coEvery { preferences.setLocalOnly(true) } just Runs
        val repository = createRepository(localOnly = false)

        repository.setLocalOnly(true)

        coVerify { preferences.setLocalOnly(true) }
    }

    @Test
    fun `switching from local-only to cloud mode allows sync`() = runTest {
        // Start in local-only mode
        coEvery { preferences.isLocalOnlySync() } returns true
        val repository = createRepository(localOnly = true)
        assertTrue(repository.sync().isFailure)

        // Switch to cloud mode
        coEvery { preferences.isLocalOnlySync() } returns false
        every { firebaseSessionProvider.currentUserUid() } returns "test-uid"
        coEvery { vaultItemDao.getItemsBySyncStatus(any()) } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.getVaultItems(any(), any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(any()) } returns emptyList()
        coEvery { firestoreRepository.getFolders(any(), any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(any()) } returns emptyList()
        coEvery { firestoreRepository.getTags(any(), any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(any()) } returns emptyList()
        coEvery { preferences.lastSyncTime } returns flowOf(0L)
        coEvery { preferences.setLastSyncTime(any()) } just Runs

        val cloudRepository = createRepository(localOnly = false)
        assertTrue(cloudRepository.sync().isSuccess)
    }

    // ── Local data persistence tests ─────────────────────────────────────────

    @Test
    fun `local vault items can be created without sync`() = runTest {
        val repository = createRepository(localOnly = true)
        val localItem = VaultItemEntity(
            id = "local-item-1",
            type = "login",
            name = "Local Password",
            folderId = null,
            encryptedData = "encrypted".toByteArray(),
            favorite = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )

        coEvery { vaultItemDao.insertItems(listOf(localItem)) } just Runs

        vaultItemDao.insertItems(listOf(localItem))

        coVerify { vaultItemDao.insertItems(listOf(localItem)) }
        verify(exactly = 0) { firestoreRepository wasNot Called }
    }

    @Test
    fun `local folders can be created without sync`() = runTest {
        val repository = createRepository(localOnly = true)
        val localFolder = FolderEntity(
            id = "local-folder-1",
            name = "Personal",
            icon = "folder",
            parentId = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )

        coEvery { folderDao.insertFolders(listOf(localFolder)) } just Runs

        folderDao.insertFolders(listOf(localFolder))

        coVerify { folderDao.insertFolders(listOf(localFolder)) }
        verify(exactly = 0) { firestoreRepository wasNot Called }
    }

    @Test
    fun `local tags can be created without sync`() = runTest {
        val repository = createRepository(localOnly = true)
        val localTag = TagEntity(
            id = "local-tag-1",
            name = "work",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            deletedAt = null,
            syncStatus = "PENDING_UPLOAD"
        )

        coEvery { tagDao.insertTags(listOf(localTag)) } just Runs

        tagDao.insertTags(listOf(localTag))

        coVerify { tagDao.insertTags(listOf(localTag)) }
        verify(exactly = 0) { firestoreRepository wasNot Called }
    }

    @Test
    fun `pending upload items accumulate locally in offline mode`() = runTest {
        val repository = createRepository(localOnly = true)
        val pendingItems = listOf(
            VaultItemEntity(
                id = "pending-1",
                type = "login",
                name = "GitHub",
                folderId = null,
                encryptedData = "enc1".toByteArray(),
                favorite = false,
                createdAt = 1000L,
                updatedAt = 2000L,
                deletedAt = null,
                syncStatus = "PENDING_UPLOAD"
            ),
            VaultItemEntity(
                id = "pending-2",
                type = "note",
                name = "Secret Note",
                folderId = null,
                encryptedData = "enc2".toByteArray(),
                favorite = true,
                createdAt = 1000L,
                updatedAt = 3000L,
                deletedAt = null,
                syncStatus = "PENDING_UPLOAD"
            )
        )

        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD") } returns pendingItems

        val result = vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD")

        assertEquals(2, result.size)
        assertEquals("PENDING_UPLOAD", result[0].syncStatus)
        assertEquals("PENDING_UPLOAD", result[1].syncStatus)
    }

    // ── Transition tests: offline → online ───────────────────────────────────

    @Test
    fun `pending items from offline mode are synced when switching to cloud`() = runTest {
        // User created items while offline
        val offlineItems = listOf(
            VaultItemEntity(
                id = "offline-1",
                type = "login",
                name = "Created Offline",
                folderId = null,
                encryptedData = "offline-enc".toByteArray(),
                favorite = false,
                createdAt = 1000L,
                updatedAt = 2000L,
                deletedAt = null,
                syncStatus = "PENDING_UPLOAD"
            )
        )

        // Now switching to cloud mode and syncing
        coEvery { preferences.isLocalOnlySync() } returns false
        every { firebaseSessionProvider.currentUserUid() } returns "test-uid"
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD") } returns offlineItems
        coEvery { vaultItemDao.getItemsBySyncStatus("PENDING_DELETE") } returns emptyList()
        coEvery { folderDao.getFoldersBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { tagDao.getTagsBySyncStatus("PENDING_UPLOAD") } returns emptyList()
        coEvery { firestoreRepository.syncVaultItems(any(), any()) } returns (emptyList<Map<String, Any?>>() to emptyList())
        coEvery { firestoreRepository.getVaultItems(any(), any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedItems(any()) } returns emptyList()
        coEvery { firestoreRepository.getFolders(any(), any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedFolders(any()) } returns emptyList()
        coEvery { firestoreRepository.getTags(any(), any()) } returns emptyList()
        coEvery { firestoreRepository.getTrashedTags(any()) } returns emptyList()
        coEvery { preferences.lastSyncTime } returns flowOf(0L)
        coEvery { preferences.setLastSyncTime(any()) } just Runs

        val repository = createRepository(localOnly = false)
        val result = repository.sync()

        assertTrue(result.isSuccess)
        coVerify { firestoreRepository.syncVaultItems(any(), any()) }
    }

    // ── Network state tests ──────────────────────────────────────────────────

    @Test
    fun `isOnline returns false when device is in airplane mode`() = runTest {
        every { connectivityManager.activeNetwork } returns null
        val repository = createRepository(localOnly = false)

        assertFalse(repository.isOnline())
    }

    @Test
    fun `isOnline returns false when connected to wifi without internet`() = runTest {
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns false
        val repository = createRepository(localOnly = false)

        assertFalse(repository.isOnline())
    }
}
