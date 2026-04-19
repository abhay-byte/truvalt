package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.FirebaseSessionProvider
import com.ivarna.truvalt.data.remote.FirestoreVaultRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncRepositoryImplTest {

    private val context = mockk<Context>(relaxed = true)
    private val preferences = mockk<TruvaltPreferences>(relaxed = true)
    private val vaultItemDao = mockk<VaultItemDao>(relaxed = true)
    private val folderDao = mockk<FolderDao>(relaxed = true)
    private val tagDao = mockk<TagDao>(relaxed = true)
    private val firebaseSessionProvider = object : FirebaseSessionProvider {
        override fun currentUserUid(): String? = null
    }
    private val firestoreRepository = mockk<FirestoreVaultRepository>(relaxed = true)

    @Test
    fun `sync fails fast when firebase user is missing`() = runTest {
        val connectivityManager = mockk<ConnectivityManager>()
        val activeNetwork = mockk<Network>()
        val networkCapabilities = mockk<NetworkCapabilities>()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns activeNetwork
        every { connectivityManager.getNetworkCapabilities(activeNetwork) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true

        val repository = SyncRepositoryImpl(
            context = context,
            preferences = preferences,
            vaultItemDao = vaultItemDao,
            folderDao = folderDao,
            tagDao = tagDao,
            firebaseSessionProvider = firebaseSessionProvider,
            firestoreRepository = firestoreRepository
        )

        val result = repository.sync()

        assertTrue(result.isFailure)
        assertEquals("Not signed in to Firebase", result.exceptionOrNull()?.message)
    }
}
