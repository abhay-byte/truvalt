package com.ivarna.truvalt.autofill

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
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

    private val tag = "TruvaltAutofill"

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

        val targetPackageName = structure.activityComponent.packageName
        val appPackageName = this.packageName
        
        // Parse the structure to find autofillable fields
        val parsedStructure = parseStructure(structure)
        Log.d(
            tag,
            "onFillRequest package=$targetPackageName usernameId=${parsedStructure.usernameId != null} passwordId=${parsedStructure.passwordId != null} webDomain=${parsedStructure.webDomain}"
        )
        if (parsedStructure.usernameId == null && parsedStructure.passwordId == null) {
            callback.onSuccess(null)
            return
        }
        val clientState = buildClientState(parsedStructure)

        // Check if vault is unlocked
        if (!vaultKeyManager.hasKey()) {
            // Show authentication prompt
            val authIntent = Intent(this, AutofillAuthActivity::class.java).apply {
                putExtra("package_name", targetPackageName)
                putExtra("request_type", "fill")
            }
            val authPendingIntent = PendingIntent.getActivity(
                this,
                AUTOFILL_AUTH_REQUEST_CODE,
                authIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val presentation = RemoteViews(appPackageName, R.layout.autofill_auth_prompt).apply {
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
                .setClientState(clientState)
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
                    .filter { matchesPackageOrDomain(it, targetPackageName, parsedStructure.webDomain) }

                if (credentials.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val datasets = credentials.take(MAX_DATASETS).map { cred ->
                    createDataset(cred, parsedStructure, appPackageName)
                }

                val responseBuilder = FillResponse.Builder()
                    .setClientState(clientState)
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

        val clientState = request.clientState
        val usernameId = clientState?.getAutofillId(EXTRA_USERNAME_ID)
        val passwordId = clientState?.getAutofillId(EXTRA_PASSWORD_ID)
        val webDomain = clientState?.getString(EXTRA_WEB_DOMAIN)

        val parsedStructure = parseStructure(structure)
        val username = usernameId?.let { extractTextValue(request.fillContexts, it) }
            ?: parsedStructure.usernameValue
        val password = passwordId?.let { extractTextValue(request.fillContexts, it) }
            ?: parsedStructure.passwordValue

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            callback.onFailure("Missing credentials")
            return
        }

        // Launch save activity
        val saveIntent = Intent(this, AutofillSaveActivity::class.java).apply {
            putExtra("username", username)
            putExtra("password", password)
            putExtra(
                "url",
                canonicalizeStorageTarget(webDomain ?: parsedStructure.webDomain ?: structure.activityComponent.packageName)
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(saveIntent)
        callback.onSuccess()
    }

    private fun parseStructure(structure: AssistStructure): ParsedStructure {
        var usernameMatch: FieldMatch? = null
        var passwordMatch: FieldMatch? = null
        var webDomain: String? = null
        var traversalIndex = 0

        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode
            traverseViewNode(viewNode) { node ->
                val hints = node.autofillHints?.toList() ?: emptyList()
                val inputType = node.inputType
                val autofillValue = node.autofillValue
                val htmlAttributes = node.htmlInfo?.attributes ?: emptyList()
                val htmlAutocomplete = htmlAttributeValue(htmlAttributes, "autocomplete")
                val htmlInputType = htmlAttributeValue(htmlAttributes, "type")
                val viewHint = node.hint?.toString()

                val passwordField = isPasswordField(inputType, hints, viewHint, htmlAutocomplete, htmlInputType)
                val usernameField = !passwordField &&
                    isUsernameField(inputType, hints, viewHint, htmlAutocomplete, htmlInputType)

                if (passwordField) {
                    buildFieldMatch(
                            node = node,
                            value = autofillValue?.textValue?.toString(),
                            role = FieldRole.PASSWORD,
                            order = traversalIndex,
                            hints = hints,
                            viewHint = viewHint,
                            htmlAutocomplete = htmlAutocomplete,
                            htmlInputType = htmlInputType,
                            inputType = inputType
                        )?.let {
                        passwordMatch = selectBetterMatch(passwordMatch, it)
                    }
                } else if (usernameField) {
                    buildFieldMatch(
                            node = node,
                            value = autofillValue?.textValue?.toString(),
                            role = FieldRole.USERNAME,
                            order = traversalIndex,
                            hints = hints,
                            viewHint = viewHint,
                            htmlAutocomplete = htmlAutocomplete,
                            htmlInputType = htmlInputType,
                            inputType = inputType
                        )?.let {
                        usernameMatch = selectBetterMatch(usernameMatch, it)
                    }
                }

                // Get web domain for WebView
                if (node.webDomain != null) {
                    webDomain = node.webDomain
                }
                traversalIndex++
            }
        }

        return ParsedStructure(
            usernameMatch?.id,
            passwordMatch?.id,
            usernameMatch?.value,
            passwordMatch?.value,
            webDomain
        ).also {
            Log.d(
                tag,
                "parseStructure usernameMatch=${usernameMatch?.score}:${usernameMatch?.order} passwordMatch=${passwordMatch?.score}:${passwordMatch?.order} webDomain=$webDomain"
            )
        }
    }

    private fun traverseViewNode(node: AssistStructure.ViewNode, action: (AssistStructure.ViewNode) -> Unit) {
        action(node)
        for (i in 0 until node.childCount) {
            traverseViewNode(node.getChildAt(i), action)
        }
    }

    private fun isUsernameField(
        inputType: Int,
        hints: List<String>,
        viewHint: String?,
        htmlAutocomplete: String?,
        htmlInputType: String?
    ): Boolean {
        return AutofillFieldClassifier.isUsernameField(
            inputType = inputType,
            hints = hints,
            viewHint = viewHint,
            htmlAutocomplete = htmlAutocomplete,
            htmlInputType = htmlInputType
        )
    }

    private fun isPasswordField(
        inputType: Int,
        hints: List<String>,
        viewHint: String?,
        htmlAutocomplete: String?,
        htmlInputType: String?
    ): Boolean {
        return AutofillFieldClassifier.isPasswordField(
            inputType = inputType,
            hints = hints,
            viewHint = viewHint,
            htmlAutocomplete = htmlAutocomplete,
            htmlInputType = htmlInputType
        )
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
        val storedTarget = canonicalizeStorageTarget(item.url) ?: return false
        val normalizedPackage = packageName.trim().lowercase()
        if (storedTarget == normalizedPackage) return true

        val normalizedWebDomain = webDomain?.let(::canonicalizeStorageTarget) ?: return false
        return hostMatches(storedTarget, normalizedWebDomain)
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

    private fun buildClientState(parsedStructure: ParsedStructure): Bundle {
        return Bundle().apply {
            parsedStructure.usernameId?.let { putParcelable(EXTRA_USERNAME_ID, it) }
            parsedStructure.passwordId?.let { putParcelable(EXTRA_PASSWORD_ID, it) }
            parsedStructure.webDomain?.let { putString(EXTRA_WEB_DOMAIN, it) }
        }
    }

    private fun extractTextValue(fillContexts: List<FillContext>, targetId: AutofillId): String? {
        for (context in fillContexts.asReversed()) {
            val structure = context.structure ?: continue
            for (i in 0 until structure.windowNodeCount) {
                val rootNode = structure.getWindowNodeAt(i).rootViewNode
                val matchedNode = findViewNodeByAutofillId(rootNode, targetId) ?: continue
                val value = matchedNode.autofillValue?.textValue?.toString()
                    ?: matchedNode.text?.toString()
                if (!value.isNullOrBlank()) {
                    return value
                }
            }
        }
        return null
    }

    private fun findViewNodeByAutofillId(
        node: AssistStructure.ViewNode,
        targetId: AutofillId
    ): AssistStructure.ViewNode? {
        if (node.autofillId == targetId) {
            return node
        }
        for (i in 0 until node.childCount) {
            val child = findViewNodeByAutofillId(node.getChildAt(i), targetId)
            if (child != null) {
                return child
            }
        }
        return null
    }

    private fun buildFieldMatch(
        node: AssistStructure.ViewNode,
        value: String?,
        role: FieldRole,
        order: Int,
        hints: List<String>,
        viewHint: String?,
        htmlAutocomplete: String?,
        htmlInputType: String?,
        inputType: Int
    ): FieldMatch? {
        val autofillId = node.autofillId ?: return null
        var score = 0
        val normalizedAutocomplete = htmlAutocomplete?.lowercase()
        val normalizedHtmlInputType = htmlInputType?.lowercase()
        val normalizedViewHint = viewHint?.lowercase()

        when (role) {
            FieldRole.USERNAME -> {
                if (normalizedAutocomplete == "username" ||
                    normalizedAutocomplete == "email" ||
                    normalizedAutocomplete == "emailaddress"
                ) {
                    score += 100
                } else if (normalizedAutocomplete?.contains("username") == true ||
                    normalizedAutocomplete?.contains("email") == true
                ) {
                    score += 90
                }
                if (inputType and android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS != 0 ||
                    inputType and android.text.InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS != 0
                ) {
                    score += 80
                }
                if (hints.any { it.contains("username", ignoreCase = true) || it.contains("email", ignoreCase = true) }) {
                    score += 70
                }
                if (normalizedViewHint?.contains("username") == true ||
                    normalizedViewHint?.contains("email") == true ||
                    normalizedViewHint?.contains("login") == true ||
                    normalizedViewHint?.contains("user") == true
                ) {
                    score += 85
                }
                if (normalizedHtmlInputType == "email") {
                    score += 20
                }
            }

            FieldRole.PASSWORD -> {
                if (normalizedAutocomplete == "current-password" ||
                    normalizedAutocomplete == "new-password" ||
                    normalizedAutocomplete == "password"
                ) {
                    score += 100
                } else if (normalizedAutocomplete?.contains("password") == true) {
                    score += 90
                }
                if (inputType and android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD != 0 ||
                    inputType and android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD != 0
                ) {
                    score += 80
                }
                if (hints.any { it.contains("password", ignoreCase = true) }) {
                    score += 70
                }
                if (normalizedViewHint?.contains("password") == true) {
                    score += 85
                }
                if (normalizedHtmlInputType == "password") {
                    score += 20
                }
            }
        }

        return FieldMatch(
            id = autofillId,
            value = value,
            score = score,
            order = order
        )
    }

    private fun selectBetterMatch(current: FieldMatch?, candidate: FieldMatch): FieldMatch {
        if (current == null) return candidate
        return when {
            candidate.score > current.score -> candidate
            candidate.score < current.score -> current
            candidate.order < current.order -> candidate
            else -> current
        }
    }

    data class ParsedStructure(
        val usernameId: AutofillId?,
        val passwordId: AutofillId?,
        val usernameValue: String?,
        val passwordValue: String?,
        val webDomain: String?
    )

    private data class FieldMatch(
        val id: AutofillId,
        val value: String?,
        val score: Int,
        val order: Int
    )

    private enum class FieldRole {
        USERNAME,
        PASSWORD
    }

    companion object {
        private const val MAX_DATASETS = 5
        private const val AUTOFILL_AUTH_REQUEST_CODE = 1001
        private const val EXTRA_USERNAME_ID = "username_id"
        private const val EXTRA_PASSWORD_ID = "password_id"
        private const val EXTRA_WEB_DOMAIN = "web_domain"
    }
}

// Helper constants for View.AUTOFILL_HINT_* (available from API 26)
private object View {
    const val AUTOFILL_HINT_USERNAME = "username"
    const val AUTOFILL_HINT_PASSWORD = "password"
    const val AUTOFILL_HINT_EMAIL_ADDRESS = "emailAddress"
}

private fun canonicalizeStorageTarget(rawTarget: String): String? {
    val trimmed = rawTarget.trim()
    if (trimmed.isEmpty()) return null

    val hostFromUri = runCatching { Uri.parse(trimmed).host }.getOrNull()
        ?.takeIf { it.isNotBlank() }
        ?.let(::normalizeHost)
    if (hostFromUri != null) return hostFromUri

    val stripped = trimmed
        .removePrefix("https://")
        .removePrefix("http://")
        .substringBefore('/')
        .substringBefore('?')
        .substringBefore('#')
    return normalizeHost(stripped)
}

private fun normalizeHost(host: String): String {
    return host.trim().lowercase().removePrefix("www.")
}

private fun hostMatches(stored: String, requested: String): Boolean {
    if (stored == requested) return true
    return stored.endsWith(".$requested") || requested.endsWith(".$stored")
}

private fun htmlAttributeValue(
    attributes: List<android.util.Pair<String, String>>,
    key: String
): String? {
    return attributes.firstOrNull { it.first.equals(key, ignoreCase = true) }?.second
}

@Suppress("DEPRECATION")
private fun Bundle.getAutofillId(key: String): AutofillId? {
    return getParcelable(key) as? AutofillId
}
