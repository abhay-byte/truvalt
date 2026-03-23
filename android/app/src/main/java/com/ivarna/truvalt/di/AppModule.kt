package com.ivarna.truvalt.di

import android.content.Context
import androidx.room.Room
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.database.TruvaltDatabase
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.api.BackendApiFactory
import com.ivarna.truvalt.data.repository.AuthRepositoryImpl
import com.ivarna.truvalt.data.repository.SyncRepositoryImpl
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import com.ivarna.truvalt.domain.repository.AuthRepository
import com.ivarna.truvalt.domain.repository.SyncRepository
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TruvaltDatabase {
        return Room.databaseBuilder(
            context,
            TruvaltDatabase::class.java,
            "truvalt_vault.db"
        ).build()
    }

    @Provides
    fun provideVaultItemDao(database: TruvaltDatabase): VaultItemDao {
        return database.vaultItemDao()
    }

    @Provides
    fun provideFolderDao(database: TruvaltDatabase): FolderDao {
        return database.folderDao()
    }

    @Provides
    fun provideTagDao(database: TruvaltDatabase): TagDao {
        return database.tagDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCryptoManager(): CryptoManager {
        return CryptoManager()
    }

    @Provides
    @Singleton
    fun provideTruvaltPreferences(@ApplicationContext context: Context): TruvaltPreferences {
        return TruvaltPreferences(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApiFactory(okHttpClient: OkHttpClient): BackendApiFactory {
        return BackendApiFactory(okHttpClient)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVaultRepository(impl: VaultRepositoryImpl): VaultRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository
}
