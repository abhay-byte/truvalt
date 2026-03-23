package com.ivarna.truvalt.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackendApiFactory @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    fun create(serverUrl: String): TruvaltApiService {
        return Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(serverUrl))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TruvaltApiService::class.java)
    }

    fun normalizeBaseUrl(serverUrl: String): String {
        val trimmed = serverUrl.trim().trimEnd('/')
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }

        return "$withScheme/"
    }
}
