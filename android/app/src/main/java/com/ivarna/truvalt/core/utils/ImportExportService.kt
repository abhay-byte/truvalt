package com.ivarna.truvalt.core.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.domain.model.Folder
import com.ivarna.truvalt.domain.model.Tag
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemTag
import java.util.Base64
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportExportService @Inject constructor(
    private val cryptoManager: CryptoManager
) {

    private val gson = Gson()

    sealed class ImportResult {
        data class Success(
            val items: List<VaultItem>,
            val folders: List<Folder>,
            val tags: List<Tag>,
            val itemTags: List<VaultItemTag>,
            val errors: List<String>
        ) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }

    enum class ImportFormat {
        BITWARDEN_JSON,
        LASTPASS_CSV,
        CHROME_CSV,
        FIREFOX_CSV,
        GENERIC_CSV,
        TRUVALT_ENCRYPTED,
        TRUVALT_JSON
    }

    enum class ExportFormat {
        TRUVALT_ENCRYPTED,
        JSON,
        CSV
    }

    data class VaultExportData(
        val items: List<VaultItem>,
        val folders: List<Folder>,
        val tags: List<Tag>,
        val itemTags: List<VaultItemTag>
    )

    fun importData(content: String, format: ImportFormat, vaultKey: ByteArray? = null): ImportResult {
        return try {
            when (format) {
                ImportFormat.BITWARDEN_JSON -> importBitwardenJson(content)
                ImportFormat.LASTPASS_CSV -> importLastPassCsv(content)
                ImportFormat.CHROME_CSV -> importChromeCsv(content)
                ImportFormat.FIREFOX_CSV -> importFirefoxCsv(content)
                ImportFormat.GENERIC_CSV -> importGenericCsv(content)
                ImportFormat.TRUVALT_ENCRYPTED -> {
                    if (vaultKey == null) {
                        return ImportResult.Error("Vault must be unlocked to import encrypted .truvalt file")
                    }
                    importTruvaltEncrypted(content, vaultKey)
                }
                ImportFormat.TRUVALT_JSON -> importTruvaltJson(content)
            }
        } catch (e: Exception) {
            ImportResult.Error("Import failed: ${e.message}")
        }
    }

    fun exportToEncryptedTruvalt(data: VaultExportData, vaultKey: ByteArray): String {
        val innerJson = buildExportJson(data)
        val innerBytes = innerJson.toString().toByteArray(Charsets.UTF_8)

        val blob = cryptoManager.encryptVaultItem(innerBytes, vaultKey)

        val envelope = JsonObject().apply {
            addProperty("version", 2)
            addProperty("exported_at", System.currentTimeMillis())
            addProperty("format", "truvalt_vault_key")
            addProperty("iv", Base64.getEncoder().encodeToString(blob.iv))
            addProperty("ciphertext", Base64.getEncoder().encodeToString(blob.ciphertext))
        }

        return gson.toJson(envelope)
    }

    fun exportToJson(data: VaultExportData): String {
        return gson.toJson(buildExportJson(data))
    }

    fun exportToCsv(data: VaultExportData): String {
        val sb = StringBuilder()
        sb.appendLine("name,type,url,username,password,notes")

        data.items.forEach { item ->
            try {
                val payloadStr = String(item.encryptedData, Charsets.UTF_8)
                val payload = runCatching { JsonParser.parseString(payloadStr).asJsonObject }.getOrNull()

                when (item.type) {
                    "login" -> {
                        val url = payload?.get("url")?.asString ?: ""
                        val username = payload?.get("username")?.asString ?: ""
                        val password = payload?.get("password")?.asString ?: ""
                        val notes = payload?.get("notes")?.asString ?: ""
                        sb.appendLine(escapeCsv(item.name) + "," + escapeCsv("login") + "," + escapeCsv(url) + "," + escapeCsv(username) + "," + escapeCsv(password) + "," + escapeCsv(notes))
                    }
                    else -> {
                        sb.appendLine(escapeCsv(item.name) + "," + escapeCsv(item.type) + ",,,," + escapeCsv(payloadStr))
                    }
                }
            } catch (e: Exception) {
                // Skip items that can't be parsed
            }
        }

        return sb.toString()
    }

    private fun buildExportJson(data: VaultExportData): JsonObject {
        val itemsArray = JsonArray()
        data.items.forEach { item ->
            val payload = try {
                JsonParser.parseString(String(item.encryptedData, Charsets.UTF_8)).asJsonObject
            } catch (e: Exception) {
                JsonObject().apply { addProperty("raw", String(item.encryptedData, Charsets.UTF_8)) }
            }

            itemsArray.add(JsonObject().apply {
                addProperty("id", item.id)
                addProperty("type", item.type)
                addProperty("name", item.name)
                addProperty("folder_id", item.folderId)
                addProperty("favorite", item.favorite)
                addProperty("created_at", item.createdAt)
                addProperty("updated_at", item.updatedAt)
                add("payload", payload)
            })
        }

        val foldersArray = JsonArray()
        data.folders.forEach { folder ->
            foldersArray.add(JsonObject().apply {
                addProperty("id", folder.id)
                addProperty("name", folder.name)
                addProperty("icon", folder.icon)
                addProperty("parent_id", folder.parentId)
                addProperty("created_at", folder.createdAt)
                addProperty("updated_at", folder.updatedAt)
            })
        }

        val tagsArray = JsonArray()
        data.tags.forEach { tag ->
            tagsArray.add(JsonObject().apply {
                addProperty("id", tag.id)
                addProperty("name", tag.name)
                addProperty("created_at", tag.createdAt)
                addProperty("updated_at", tag.updatedAt)
            })
        }

        val itemTagsArray = JsonArray()
        data.itemTags.forEach { mapping ->
            itemTagsArray.add(JsonObject().apply {
                addProperty("item_id", mapping.itemId)
                addProperty("tag_id", mapping.tagId)
            })
        }

        return JsonObject().apply {
            addProperty("version", 1)
            addProperty("exported_at", System.currentTimeMillis())
            add("items", itemsArray)
            add("folders", foldersArray)
            add("tags", tagsArray)
            add("item_tags", itemTagsArray)
        }
    }

    private fun importTruvaltEncrypted(content: String, vaultKey: ByteArray): ImportResult {
        val envelope = JsonParser.parseString(content).asJsonObject
        val iv = Base64.getDecoder().decode(envelope.get("iv").asString)
        val ciphertext = Base64.getDecoder().decode(envelope.get("ciphertext").asString)

        val blob = com.ivarna.truvalt.core.crypto.EncryptedBlob(iv, ciphertext)
        val decryptedBytes = cryptoManager.decryptVaultItem(blob, vaultKey)
        val decryptedJson = String(decryptedBytes, Charsets.UTF_8)

        return importTruvaltJson(decryptedJson)
    }

    private fun importTruvaltJson(content: String): ImportResult {
        val errors = mutableListOf<String>()
        val items = mutableListOf<VaultItem>()
        val folders = mutableListOf<Folder>()
        val tags = mutableListOf<Tag>()
        val itemTags = mutableListOf<VaultItemTag>()

        val json = JsonParser.parseString(content).asJsonObject

        // Import folders
        if (json.has("folders")) {
            json.getAsJsonArray("folders").forEach { element ->
                try {
                    val f = element.asJsonObject
                    folders.add(Folder(
                        id = f.get("id").asString,
                        name = f.get("name").asString,
                        icon = f.get("icon")?.takeIf { !it.isJsonNull }?.asString,
                        parentId = f.get("parent_id")?.takeIf { !it.isJsonNull }?.asString,
                        createdAt = f.get("created_at")?.asLong ?: System.currentTimeMillis(),
                        updatedAt = f.get("updated_at")?.asLong ?: System.currentTimeMillis()
                    ))
                } catch (e: Exception) {
                    errors.add("Failed to parse folder: ${e.message}")
                }
            }
        }

        // Import tags
        if (json.has("tags")) {
            json.getAsJsonArray("tags").forEach { element ->
                try {
                    val t = element.asJsonObject
                    tags.add(Tag(
                        id = t.get("id").asString,
                        name = t.get("name").asString,
                        createdAt = t.get("created_at")?.asLong ?: System.currentTimeMillis(),
                        updatedAt = t.get("updated_at")?.asLong ?: System.currentTimeMillis()
                    ))
                } catch (e: Exception) {
                    errors.add("Failed to parse tag: ${e.message}")
                }
            }
        }

        // Import item-tag mappings
        if (json.has("item_tags")) {
            json.getAsJsonArray("item_tags").forEach { element ->
                try {
                    val m = element.asJsonObject
                    itemTags.add(VaultItemTag(
                        itemId = m.get("item_id").asString,
                        tagId = m.get("tag_id").asString
                    ))
                } catch (e: Exception) {
                    errors.add("Failed to parse item-tag mapping: ${e.message}")
                }
            }
        }

        // Import items
        if (json.has("items")) {
            json.getAsJsonArray("items").forEach { element ->
                try {
                    val itemJson = element.asJsonObject
                    val type = itemJson.get("type").asString
                    val name = itemJson.get("name").asString
                    val id = itemJson.get("id")?.asString ?: UUID.randomUUID().toString()
                    val folderId = itemJson.get("folder_id")?.takeIf { !it.isJsonNull }?.asString
                    val favorite = itemJson.get("favorite")?.asBoolean ?: false
                    val createdAt = itemJson.get("created_at")?.asLong ?: System.currentTimeMillis()
                    val updatedAt = itemJson.get("updated_at")?.asLong ?: System.currentTimeMillis()

                    val payload = when {
                        itemJson.has("payload") -> {
                            gson.toJson(itemJson.getAsJsonObject("payload")).toByteArray(Charsets.UTF_8)
                        }
                        itemJson.has("data") -> {
                            Base64.getDecoder().decode(itemJson.get("data").asString)
                        }
                        else -> {
                            itemJson.get("notes")?.asString?.toByteArray(Charsets.UTF_8) ?: ByteArray(0)
                        }
                    }

                    items.add(VaultItem(
                        id = id,
                        type = type,
                        name = name,
                        folderId = folderId,
                        encryptedData = payload,
                        favorite = favorite,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    ))
                } catch (e: Exception) {
                    errors.add("Failed to parse item: ${e.message}")
                }
            }
        }

        return ImportResult.Success(items, folders, tags, itemTags, errors)
    }

    private fun importBitwardenJson(content: String): ImportResult {
        val errors = mutableListOf<String>()
        val items = mutableListOf<VaultItem>()

        try {
            val json = gson.fromJson(content, JsonObject::class.java)
            val folders = mutableMapOf<String, String>()

            if (json.has("folders")) {
                json.getAsJsonArray("folders").forEach { folder ->
                    val f = folder.asJsonObject
                    folders[f.get("id").asString] = f.get("name").asString
                }
            }

            if (json.has("items")) {
                json.getAsJsonArray("items").forEach { item ->
                    try {
                        val i = item.asJsonObject
                        val type = when (i.get("type").asInt) {
                            1 -> "login"
                            2 -> "secure_note"
                            3 -> "credit_card"
                            else -> "login"
                        }

                        val name = i.get("name")?.asString ?: "Unknown"
                        val login = i.getAsJsonObject("login")
                        val notes = i.get("notes")?.asString ?: ""

                        val payload = JsonObject().apply {
                            if (login != null) {
                                addProperty("url", login.get("uri")?.asString ?: "")
                                addProperty("username", login.get("username")?.asString ?: "")
                                addProperty("password", login.get("password")?.asString ?: "")
                            }
                            addProperty("notes", notes)
                        }

                        items.add(
                            VaultItem(
                                id = UUID.randomUUID().toString(),
                                type = type,
                                name = name,
                                encryptedData = gson.toJson(payload).toByteArray(Charsets.UTF_8)
                            )
                        )
                    } catch (e: Exception) {
                        errors.add("Failed to parse item: ${e.message}")
                    }
                }
            }

            return ImportResult.Success(items, emptyList(), emptyList(), emptyList(), errors)
        } catch (e: Exception) {
            return ImportResult.Error("Invalid Bitwarden format: ${e.message}")
        }
    }

    private fun importLastPassCsv(content: String): ImportResult {
        val errors = mutableListOf<String>()
        val items = mutableListOf<VaultItem>()

        val lines = content.lines()
        if (lines.isEmpty()) return ImportResult.Error("Empty file")

        val headers = lines[0].split(",").map { it.trim().lowercase() }
        val urlIndex = headers.indexOf("url")
        val usernameIndex = headers.indexOf("username")
        val passwordIndex = headers.indexOf("password")
        val extraIndex = headers.indexOf("extra")
        val nameIndex = headers.indexOf("name")

        for (i in 1 until lines.size) {
            try {
                val values = parseCsvLine(lines[i])
                if (values.size < 3) continue

                val url = urlIndex.takeIf { it >= 0 }?.let { values.getOrNull(it) } ?: ""
                val username = usernameIndex.takeIf { it >= 0 }?.let { values.getOrNull(it) } ?: ""
                val password = passwordIndex.takeIf { it >= 0 }?.let { values.getOrNull(it) } ?: ""
                val notes = extraIndex.takeIf { it >= 0 }?.let { values.getOrNull(it) } ?: ""
                val name = nameIndex.takeIf { it >= 0 }?.let { values.getOrNull(it) } ?: url

                if (name.isNotBlank()) {
                    val payload = JsonObject().apply {
                        addProperty("url", url)
                        addProperty("username", username)
                        addProperty("password", password)
                        addProperty("notes", notes)
                    }
                    items.add(
                        VaultItem(
                            id = UUID.randomUUID().toString(),
                            type = "login",
                            name = name,
                            encryptedData = gson.toJson(payload).toByteArray(Charsets.UTF_8)
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("Failed to parse line $i: ${e.message}")
            }
        }

        return ImportResult.Success(items, emptyList(), emptyList(), emptyList(), errors)
    }

    private fun importChromeCsv(content: String): ImportResult {
        return importGenericCsv(content, "chrome")
    }

    private fun importFirefoxCsv(content: String): ImportResult {
        return importGenericCsv(content, "firefox")
    }

    private fun importGenericCsv(content: String, source: String = "generic"): ImportResult {
        val errors = mutableListOf<String>()
        val items = mutableListOf<VaultItem>()

        val lines = content.lines()
        if (lines.isEmpty()) return ImportResult.Error("Empty file")

        for (i in 1 until lines.size) {
            try {
                val values = parseCsvLine(lines[i])
                if (values.size < 2) continue

                val (name, url, username, password) = when (source) {
                    "chrome" -> {
                        val name = values.getOrNull(0) ?: ""
                        val url = values.getOrNull(1) ?: ""
                        val username = values.getOrNull(2) ?: ""
                        val password = values.getOrNull(3) ?: ""
                        Quad(name, url, username, password)
                    }
                    "firefox" -> {
                        val url = values.getOrNull(0) ?: ""
                        val username = values.getOrNull(1) ?: ""
                        val password = values.getOrNull(2) ?: ""
                        val name = url
                        Quad(name, url, username, password)
                    }
                    else -> {
                        val name = values.getOrNull(0) ?: ""
                        val url = values.getOrNull(1) ?: ""
                        val username = values.getOrNull(2) ?: ""
                        val password = values.getOrNull(3) ?: ""
                        Quad(name, url, username, password)
                    }
                }

                if (name.isNotBlank()) {
                    val payload = JsonObject().apply {
                        addProperty("url", url)
                        addProperty("username", username)
                        addProperty("password", password)
                        addProperty("notes", "")
                    }
                    items.add(
                        VaultItem(
                            id = UUID.randomUUID().toString(),
                            type = "login",
                            name = name,
                            encryptedData = gson.toJson(payload).toByteArray(Charsets.UTF_8)
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("Failed to parse line $i: ${e.message}")
            }
        }

        return ImportResult.Success(items, emptyList(), emptyList(), emptyList(), errors)
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())

        return result
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
