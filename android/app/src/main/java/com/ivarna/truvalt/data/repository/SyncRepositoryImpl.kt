package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.repository.SyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: TruvaltPreferences
) : SyncRepository {

    override suspend fun sync(): Result<Unit> {
        return if (isOnline() && !isLocalOnly()) {
            try {
                preferences.setLastSyncTime(System.currentTimeMillis())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("Offline or local-only mode"))
        }
    }

    override suspend fun getLastSyncTime(): Long {
        return preferences.lastSyncTime.first()
    }

    override suspend fun setLastSyncTime(time: Long) {
        preferences.setLastSyncTime(time)
    }

    override suspend fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun isLocalOnly(): Boolean {
        return preferences.isLocalOnlySync()
    }

    override suspend fun setLocalOnly(localOnly: Boolean) {
        preferences.setLocalOnly(localOnly)
    }

    override suspend fun getServerUrl(): String? {
        return preferences.getServerUrlSync()
    }

    override suspend fun setServerUrl(url: String) {
        preferences.setServerUrl(url)
    }
}
