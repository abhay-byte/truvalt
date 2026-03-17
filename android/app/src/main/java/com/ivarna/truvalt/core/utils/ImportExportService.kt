package com.ivarna.truvalt.core.utils

import android.content.Context
import android.net.Uri
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportExportService @Inject constructor() {

    private val gson = Gson()

    sealed class ImportResult {
        data class Success(val items: List<VaultItem>, val errors: List<String>) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }

    enum class ImportFormat {
        BITWARDEN_JSON,
        LASTPASS_CSV,
        CHROME_CSV,
        FIREFOX_CSV,
        GENERIC_CSV,
        TRUVALT_EXPORT
    }

    fun importData(content: String, format: ImportFormat): ImportResult {
        return try {
            when (format) {
                ImportFormat.BITWARDEN_JSON -> importBitwardenJson(content)
                ImportFormat.LASTPASS_CSV -> importLastPassCsv(content)
                ImportFormat.CHROME_CSV -> importChromeCsv(content)
                ImportFormat.FIREFOX_CSV -> importFirefoxCsv(content)
                ImportFormat.GENERIC_CSV -> importGenericCsv(content)
                ImportFormat.TRUVALT_EXPORT -> importTruvaltExport(content)
            }
        } catch (e: Exception) {
            ImportResult.Error("Import failed: ${e.message}")
        }
    }

    private fun importBitwardenJson(content: String): ImportResult {
        val errors = mutableListOf<String>()
        val items = mutableListOf<VaultItem>()

        return try {
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

                        var data = ""
                        if (login != null) {
                            val uri = login.get("uri")?.asString ?: ""
                            val username = login.get("username")?.asString ?: ""
                            val password = login.get("password")?.asString ?: ""
                            data = "$uri|||$username|||$password|||$notes"
                        } else {
                            data = notes
                        }

                        items.add(
                            VaultItem(
                                id = UUID.randomUUID().toString(),
                                type = type,
                                name = name,
                                encryptedData = data.toByteArray(Charsets.UTF_8)
                            )
                        )
                    } catch (e: Exception) {
                        errors.add("Failed to parse item: ${e.message}")
                    }
                }
            }

            ImportResult.Success(items, errors)
        } catch (e: Exception) {
            ImportResult.Error("Invalid Bitwarden format: ${e.message}")
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
                    val data = "$url|||$username|||$password|||$notes"
                    items.add(
                        VaultItem(
                            id = UUID.randomUUID().toString(),
                            type = "login",
                            name = name,
                            encryptedData = data.toByteArray(Charsets.UTF_8)
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("Failed to parse line $i: ${e.message}")
            }
        }

        return ImportResult.Success(items, errors)
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
                    val data = "$url|||$username|||$password|||"
                    items.add(
                        VaultItem(
                            id = UUID.randomUUID().toString(),
                            type = "login",
                            name = name,
                            encryptedData = data.toByteArray(Charsets.UTF_8)
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("Failed to parse line $i: ${e.message}")
            }
        }

        return ImportResult.Success(items, errors)
    }

    private fun importTruvaltExport(content: String): ImportResult {
        return try {
            val json = gson.fromJson(content, JsonObject::class.java)
            val items = mutableListOf<VaultItem>()
            
            if (json.has("items")) {
                json.getAsJsonArray("items").forEach { item ->
                    val i = item.asJsonObject
                    val type = i.get("type").asString
                    val data = i.get("data").asString
                    
                    items.add(
                        VaultItem(
                            id = i.get("id").asString,
                            type = type,
                            name = i.get("name").asString,
                            encryptedData = android.util.Base64.decode(data, android.util.Base64.DEFAULT)
                        )
                    )
                }
            }
            
            ImportResult.Success(items, emptyList())
        } catch (e: Exception) {
            ImportResult.Error("Invalid truvalt export: ${e.message}")
        }
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

    data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
