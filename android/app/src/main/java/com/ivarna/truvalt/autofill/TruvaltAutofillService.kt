package com.ivarna.truvalt.autofill

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.*
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.ivarna.truvalt.R
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.domain.model.LoginItemData
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.EncryptedBlob
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class TruvaltAutofillService : AutofillService() {

    @Inject
    lateinit var vaultItemDao: VaultItemDao

    @Inject
    lateinit var vaultKeyManager: VaultKeyManager

    @Inject
    lateinit var cryptoManager: CryptoManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }

    override fun onConnected() {
        super.onConnected()
    }

    override fun onDisconnected() {
        super.onDisconnected()
        serviceScope.cancel()
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure
        if (structure == null) {
            callback.onSuccess(null)
            return
        }

        val packageName = structure.activityComponent.packageName
        
        // Parse the structure to find autofillable fields
        val parsedStructure = parseStructure(structure)
        if (parsedStructure.usernameId == null && parsedStructure.passwordId == null) {
            callback.onSuccess(null)
            return
        }

        // Check if vault is unlocked
        if (!vaultKeyManager.hasKey()) {
            // Show authentication prompt
            val authIntent = Intent(this, AutofillAuthActivity::class.java).apply {
                putExtra("package_name", packageName)
                putExtra("request_type", "fill")
            }
            val authPendingIntent = PendingIntent.getActivity(
                this,
                AUTOFILL_AUTH_REQUEST_CODE,
                authIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val presentation = RemoteViews(packageName, R.layout.autofill_auth_prompt).apply {
                setTextViewText(R.id.auth_prompt_text, "Unlock Truvalt to autofill")
            }
            
            val autofillIds = mutableListOf<AutofillId>()
            parsedStructure.usernameId?.let { autofillIds.add(it) }
            parsedStructure.passwordId?.let { autofillIds.add(it) }
            
            val response = FillResponse.Builder()
                .setAuthentication(
                    autofillIds.toTypedArray(),
                    authPendingIntent.intentSender,
                    presentation
                )
                .build()
            
            callback.onSuccess(response)
            return
        }

        // Get matching credentials
        serviceScope.launch {
            try {
                val items = vaultItemDao.getAllItems().first()
                    .filter { it.type == "login" && it.deletedAt == null }
                
                val credentials = items.mapNotNull { decryptLoginItem(it) }
                    .filter { matchesPackageOrDomain(it, packageName, parsedStructure.webDomain) }

                if (credentials.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val datasets = credentials.take(MAX_DATASETS).map { cred ->
                    createDataset(cred, parsedStructure, packageName)
                }

                val responseBuilder = FillResponse.Builder()
                datasets.forEach { responseBuilder.addDataset(it) }

                // Add save info if we have both username and password fields
                if (parsedStructure.usernameId != null && parsedStructure.passwordId != null) {
                    val saveIds = mutableListOf<AutofillId>()
                    parsedStructure.usernameId?.let { saveIds.add(it) }
                    parsedStructure.passwordId?.let { saveIds.add(it) }
                    
                    val saveInfo = SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        saveIds.toTypedArray()
                    ).build()
                    responseBuilder.setSaveInfo(saveInfo)
                }

                callback.onSuccess(responseBuilder.build())
            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val structure = request.fillContexts.lastOrNull()?.structure
        if (structure == null) {
            callback.onFailure("No structure to save")
            return
        }

        val parsedStructure = parseStructure(structure)
        val username = parsedStructure.usernameValue
        val password = parsedStructure.passwordValue

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            callback.onFailure("Missing credentials")
            return
        }

        // Launch save activity
        val saveIntent = Intent(this, AutofillSaveActivity::class.java).apply {
            putExtra("username", username)
            putExtra("password", password)
            putExtra("url", parsedStructure.webDomain ?: structure.activityComponent.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(saveIntent)
        callback.onSuccess()
    }

    private fun parseStructure(structure: AssistStructure): ParsedStructure {
        var usernameId: AutofillId? = null
        var passwordId: AutofillId? = null
        var usernameValue: String? = null
        var passwordValue: String? = null
        var webDomain: String? = null

        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode
            traverseViewNode(viewNode) { node ->
                val hints = node.autofillHints?.toList() ?: emptyList()
                val inputType = node.inputType
                val autofillValue = node.autofillValue
                
                // Detect username/email field
                if (hints.any { it in listOf(View.AUTOFILL_HINT_USERNAME, View.AUTOFILL_HINT_EMAIL_ADDRESS) } ||
                    isUsernameField(inputType, hints)) {
                    usernameId = node.autofillId
                    usernameValue = autofillValue?.textValue?.toString()
                }
                
                // Detect password field
                if (hints.contains(View.AUTOFILL_HINT_PASSWORD) || isPasswordField(inputType)) {
                    passwordId = node.autofillId
                    passwordValue = autofillValue?.textValue?.toString()
                }

                // Get web domain for WebView
                if (node.webDomain != null) {
                    webDomain = node.webDomain
                }
            }
        }

        return ParsedStructure(usernameId, passwordId, usernameValue, passwordValue, webDomain)
    }

    private fun traverseViewNode(node: AssistStructure.ViewNode, action: (AssistStructure.ViewNode) -> Unit) {
        action(node)
        for (i in 0 until node.childCount) {
            traverseViewNode(node.getChildAt(i), action)
        }
    }

    private fun isUsernameField(inputType: Int, hints: List<String>): Boolean {
        return inputType and android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS != 0 ||
                inputType and android.text.InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS != 0 ||
                hints.any { it.contains("email", ignoreCase = true) || it.contains("username", ignoreCase = true) }
    }

    private fun isPasswordField(inputType: Int): Boolean {
        return inputType and android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD != 0 ||
                inputType and android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD != 0
    }

    private fun decryptLoginItem(entity: VaultItemEntity): LoginItemData? {
        return try {
            val vaultKey = vaultKeyManager.getInMemoryKey() ?: return null
            // The encryptedData is stored as: iv (12 bytes) + ciphertext
            val iv = entity.encryptedData.copyOfRange(0, 12)
            val ciphertext = entity.encryptedData.copyOfRange(12, entity.encryptedData.size)
            val blob = EncryptedBlob(iv, ciphertext)
            val decrypted = cryptoManager.decryptVaultItem(blob, vaultKey)
            json.decodeFromString<LoginItemData>(String(decrypted, Charsets.UTF_8))
        } catch (e: Exception) {
            null
        }
    }

    private fun matchesPackageOrDomain(item: LoginItemData, packageName: String, webDomain: String?): Boolean {
        val itemUrl = item.url
        if (itemUrl.isBlank()) return false
        return itemUrl.contains(packageName, ignoreCase = true) ||
                (webDomain != null && itemUrl.contains(webDomain, ignoreCase = true))
    }

    private fun createDataset(
        item: LoginItemData,
        parsedStructure: ParsedStructure,
        packageName: String
    ): Dataset {
        val presentation = RemoteViews(packageName, R.layout.autofill_dataset_item).apply {
            setTextViewText(R.id.dataset_title, item.username)
            setTextViewText(R.id.dataset_subtitle, item.url)
        }

        return Dataset.Builder(presentation).apply {
            parsedStructure.usernameId?.let {
                setValue(it, AutofillValue.forText(item.username), presentation)
            }
            parsedStructure.passwordId?.let {
                setValue(it, AutofillValue.forText(item.password), presentation)
            }
        }.build()
    }

    data class ParsedStructure(
        val usernameId: AutofillId?,
        val passwordId: AutofillId?,
        val usernameValue: String?,
        val passwordValue: String?,
        val webDomain: String?
    )

    companion object {
        private const val MAX_DATASETS = 5
        private const val AUTOFILL_AUTH_REQUEST_CODE = 1001
    }
}

// Helper constants for View.AUTOFILL_HINT_* (available from API 26)
private object View {
    const val AUTOFILL_HINT_USERNAME = "username"
    const val AUTOFILL_HINT_PASSWORD = "password"
    const val AUTOFILL_HINT_EMAIL_ADDRESS = "emailAddress"
}
