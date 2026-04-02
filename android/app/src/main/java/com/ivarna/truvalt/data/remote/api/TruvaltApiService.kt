package com.ivarna.truvalt.data.remote.api

import com.ivarna.truvalt.data.remote.dto.BackendAuthRequest
import com.ivarna.truvalt.data.remote.dto.BackendAuthResponse
import com.ivarna.truvalt.data.remote.dto.BackendFolderDto
import com.ivarna.truvalt.data.remote.dto.BackendFolderRequest
import com.ivarna.truvalt.data.remote.dto.BackendGoogleLoginRequest
import com.ivarna.truvalt.data.remote.dto.BackendMessageResponse
import com.ivarna.truvalt.data.remote.dto.BackendSyncRequest
import com.ivarna.truvalt.data.remote.dto.BackendSyncResponse
import com.ivarna.truvalt.data.remote.dto.BackendTagDto
import com.ivarna.truvalt.data.remote.dto.BackendTagRequest
import com.ivarna.truvalt.data.remote.dto.BackendUserDto
import com.ivarna.truvalt.data.remote.dto.BackendVaultItemDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TruvaltApiService {
    @POST("api/register")
    suspend fun register(@Body request: BackendAuthRequest): BackendAuthResponse

    @POST("api/login")
    suspend fun login(@Body request: BackendAuthRequest): BackendAuthResponse

    @POST("api/login/google")
    suspend fun loginWithGoogle(@Body request: BackendGoogleLoginRequest): BackendAuthResponse

    @GET("api/me")
    suspend fun me(@Header("Authorization") bearerToken: String): BackendUserDto

    @POST("api/logout")
    suspend fun logout(@Header("Authorization") bearerToken: String): BackendMessageResponse

    @GET("api/health")
    suspend fun health(): BackendMessageResponse

    @GET("api/folders")
    suspend fun getFolders(@Header("Authorization") bearerToken: String): List<BackendFolderDto>

    @POST("api/folders")
    suspend fun createFolder(
        @Header("Authorization") bearerToken: String,
        @Body request: BackendFolderRequest
    ): BackendFolderDto

    @PUT("api/folders/{id}")
    suspend fun updateFolder(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String,
        @Body request: BackendFolderRequest
    ): BackendFolderDto

    @GET("api/tags")
    suspend fun getTags(@Header("Authorization") bearerToken: String): List<BackendTagDto>

    @POST("api/tags")
    suspend fun createTag(
        @Header("Authorization") bearerToken: String,
        @Body request: BackendTagRequest
    ): BackendTagDto

    @GET("api/vault/items")
    suspend fun getVaultItems(
        @Header("Authorization") bearerToken: String,
        @Query("updated_after") updatedAfter: Long? = null
    ): List<BackendVaultItemDto>

    @GET("api/vault/trash")
    suspend fun getTrashItems(@Header("Authorization") bearerToken: String): List<BackendVaultItemDto>

    @POST("api/vault/sync")
    suspend fun syncVaultItems(
        @Header("Authorization") bearerToken: String,
        @Body request: BackendSyncRequest
    ): BackendSyncResponse

    @DELETE("api/account")
    suspend fun deleteAccount(@Header("Authorization") bearerToken: String): Unit
}
