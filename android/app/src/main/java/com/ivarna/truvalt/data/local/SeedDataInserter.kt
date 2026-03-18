package com.ivarna.truvalt.data.local

import android.util.Log
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataInserter @Inject constructor(
    private val vaultItemDao: VaultItemDao,
    private val cryptoManager: CryptoManager
) {
    suspend fun insertSeedData() = withContext(Dispatchers.IO) {
        try {
            Log.d("SeedDataInserter", "Starting seed data insertion")
            val existingCount = vaultItemDao.getItemCount()
            if (existingCount > 0) {
                Log.d("SeedDataInserter", "Vault already has $existingCount items, skipping seed")
                return@withContext
            }
            
            val seedItems = SeedDataProvider.getSeedItems()
            Log.d("SeedDataInserter", "Inserting ${seedItems.size} seed items")
            
            seedItems.forEach { seed ->
                try {
                    val jsonData = JSONObject(seed.data).toString()
                    val encryptedData = cryptoManager.encryptWithKeystore(jsonData.toByteArray())
                    
                    val entity = VaultItemEntity(
                        id = seed.id,
                        type = seed.type.id,
                        name = seed.name,
                        encryptedData = encryptedData,
                        favorite = seed.favorite,
                        folderId = null,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        deletedAt = null,
                        syncStatus = "LOCAL"
                    )
                    vaultItemDao.insertItem(entity)
                    Log.d("SeedDataInserter", "Inserted: ${seed.name}")
                } catch (e: Exception) {
                    Log.e("SeedDataInserter", "Failed to insert ${seed.name}: ${e.message}", e)
                }
            }
            Log.d("SeedDataInserter", "Seed data insertion complete")
        } catch (e: Exception) {
            Log.e("SeedDataInserter", "Seed data insertion failed: ${e.message}", e)
        }
    }
}
