package com.ivarna.truvalt.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.data.local.database.TruvaltDatabase
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.model.SyncStatus
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.repository.SyncRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets

class VaultRepositoryCrudTest {

    private lateinit var context: Context
    private lateinit var database: TruvaltDatabase
    private lateinit var repository: VaultRepositoryImpl

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, TruvaltDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val cryptoManager = CryptoManager()
        val vaultKeyManager = VaultKeyManager()
        val preferences = TruvaltPreferences(context)
        val syncRepository = object : SyncRepository {
            override suspend fun sync(): Result<Unit> = Result.success(Unit)
            override suspend fun getLastSyncTime(): Long = 0L
            override suspend fun setLastSyncTime(time: Long) {}
            override suspend fun isOnline(): Boolean = true
            override suspend fun isLocalOnly(): Boolean = false
            override suspend fun setLocalOnly(localOnly: Boolean) {}
        }

        repository = VaultRepositoryImpl(
            vaultItemDao = database.vaultItemDao(),
            folderDao = database.folderDao(),
            tagDao = database.tagDao(),
            cryptoManager = cryptoManager,
            preferences = preferences,
            vaultKeyManager = vaultKeyManager,
            syncRepository = syncRepository
        )

        repository.setVaultKey(ByteArray(32) { index -> (index + 1).toByte() })
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun vaultItemCrudLifecycleWorks() = runBlocking {
        val firstItem = VaultItem(
            id = "item-1",
            type = "login",
            name = "GitHub",
            encryptedData = "secret-one".toByteArray(StandardCharsets.UTF_8),
            favorite = false,
            syncStatus = SyncStatus.SYNCED
        )

        repository.saveItem(firstItem)

        val created = repository.getItemById(firstItem.id)
        assertNotNull(created)
        assertEquals(firstItem.id, created?.id)
        assertEquals("GitHub", created?.name)
        assertEquals("secret-one", created?.encryptedData?.toString(Charsets.UTF_8))
        assertEquals(SyncStatus.PENDING_UPLOAD, created?.syncStatus)

        val updated = created!!.copy(
            name = "GitHub Personal",
            encryptedData = "secret-two".toByteArray(StandardCharsets.UTF_8)
        )
        repository.saveItem(updated)

        val afterUpdate = repository.getItemById(firstItem.id)
        assertEquals("GitHub Personal", afterUpdate?.name)
        assertEquals("secret-two", afterUpdate?.encryptedData?.toString(Charsets.UTF_8))

        repository.toggleFavorite(firstItem.id, true)
        val favorite = repository.getItemById(firstItem.id)
        assertTrue(favorite?.favorite == true)

        repository.softDeleteItem(firstItem.id)
        assertNotNull(repository.getItemById(firstItem.id)?.deletedAt)
        val trashAfterSoftDelete = repository.getTrashItems().first()
        assertTrue(trashAfterSoftDelete.any { it.id == firstItem.id })
        val activeAfterSoftDelete = repository.getAllItems().first()
        assertFalse(activeAfterSoftDelete.any { it.id == firstItem.id })

        repository.restoreItem(firstItem.id)
        val restored = repository.getItemById(firstItem.id)
        assertNotNull(restored)
        assertNull(restored?.deletedAt)

        val secondItem = VaultItem(
            id = "item-2",
            type = "login",
            name = "Example",
            encryptedData = "second-secret".toByteArray(StandardCharsets.UTF_8),
            syncStatus = SyncStatus.SYNCED
        )

        repository.saveItem(secondItem)
        repository.softDeleteItem(secondItem.id)
        repository.emptyTrash()

        assertNull(repository.getItemById(secondItem.id))

        repository.deleteItem(firstItem.id)
        assertNull(repository.getItemById(firstItem.id))
    }
}
