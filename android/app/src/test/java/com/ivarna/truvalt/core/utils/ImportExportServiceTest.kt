package com.ivarna.truvalt.core.utils

import com.google.gson.JsonParser
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.EncryptedBlob
import com.ivarna.truvalt.domain.model.Folder
import com.ivarna.truvalt.domain.model.Tag
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemTag
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ImportExportServiceTest {

    private lateinit var cryptoManager: CryptoManager
    private lateinit var importExportService: ImportExportService
    private val testVaultKey = ByteArray(32) { it.toByte() }

    @Before
    fun setup() {
        cryptoManager = mockk(relaxed = true)
        importExportService = ImportExportService(cryptoManager)
    }

    private fun createSampleData(): ImportExportService.VaultExportData {
        val folder = Folder(
            id = "folder-1",
            name = "Work",
            icon = "briefcase"
        )
        val tag = Tag(
            id = "tag-1",
            name = "important"
        )
        val item = VaultItem(
            id = "item-1",
            type = "login",
            name = "GitHub",
            folderId = "folder-1",
            encryptedData = """{"url":"https://github.com","username":"user","password":"pass123","notes":""}""".toByteArray(Charsets.UTF_8),
            favorite = true
        )
        val itemTag = VaultItemTag(itemId = "item-1", tagId = "tag-1")

        return ImportExportService.VaultExportData(
            items = listOf(item),
            folders = listOf(folder),
            tags = listOf(tag),
            itemTags = listOf(itemTag)
        )
    }

    @Test
    fun `exportToJson produces valid JSON with all data`() = runTest {
        val data = createSampleData()
        val json = importExportService.exportToJson(data)

        val parsed = JsonParser.parseString(json).asJsonObject
        assertEquals(1, parsed.get("version").asInt)
        assertTrue(parsed.has("exported_at"))
        assertEquals(1, parsed.getAsJsonArray("items").size())
        assertEquals(1, parsed.getAsJsonArray("folders").size())
        assertEquals(1, parsed.getAsJsonArray("tags").size())
        assertEquals(1, parsed.getAsJsonArray("item_tags").size())

        val item = parsed.getAsJsonArray("items").first().asJsonObject
        assertEquals("item-1", item.get("id").asString)
        assertEquals("login", item.get("type").asString)
        assertEquals("GitHub", item.get("name").asString)
        assertEquals("folder-1", item.get("folder_id").asString)
        assertEquals(true, item.get("favorite").asBoolean)
        assertEquals("https://github.com", item.getAsJsonObject("payload").get("url").asString)

        val folder = parsed.getAsJsonArray("folders").first().asJsonObject
        assertEquals("folder-1", folder.get("id").asString)
        assertEquals("Work", folder.get("name").asString)
    }

    @Test
    fun `exportToEncryptedTruvalt and import roundtrip works`() = runTest {
        val data = createSampleData()
        val innerJson = importExportService.exportToJson(data)

        every { cryptoManager.encryptVaultItem(any(), testVaultKey) } returns
            EncryptedBlob(
                iv = "iv".toByteArray(),
                ciphertext = innerJson.toByteArray(Charsets.UTF_8)
            )
        every { cryptoManager.decryptVaultItem(any(), testVaultKey) } returns
            innerJson.toByteArray(Charsets.UTF_8)

        val encrypted = importExportService.exportToEncryptedTruvalt(data, testVaultKey)
        val result = importExportService.importData(
            encrypted,
            ImportExportService.ImportFormat.TRUVALT_ENCRYPTED,
            testVaultKey
        )

        assertTrue(result is ImportExportService.ImportResult.Success)
        val success = result as ImportExportService.ImportResult.Success

        assertEquals(1, success.items.size)
        assertEquals(1, success.folders.size)
        assertEquals(1, success.tags.size)
        assertEquals(1, success.itemTags.size)

        assertEquals("GitHub", success.items[0].name)
        assertEquals("Work", success.folders[0].name)
        assertEquals("important", success.tags[0].name)
        assertEquals("item-1", success.itemTags[0].itemId)
    }

    @Test
    fun `importTruvaltJson handles legacy data field`() = runTest {
        val legacyJson = """
            {
                "version": 1,
                "items": [
                    {
                        "id": "legacy-item",
                        "type": "login",
                        "name": "Legacy",
                        "data": "bGVnYWN5LWRhdGE="
                    }
                ]
            }
        """.trimIndent()

        val result = importExportService.importData(
            legacyJson,
            ImportExportService.ImportFormat.TRUVALT_JSON
        )

        assertTrue(result is ImportExportService.ImportResult.Success)
        val success = result as ImportExportService.ImportResult.Success
        assertEquals(1, success.items.size)
        assertEquals("Legacy", success.items[0].name)
    }

    @Test
    fun `exportToCsv produces correct CSV for login items`() = runTest {
        val data = createSampleData()
        val csv = importExportService.exportToCsv(data)

        val lines = csv.lines().filter { it.isNotBlank() }
        assertEquals(2, lines.size)
        assertEquals("name,type,url,username,password,notes", lines[0])
        assertTrue(lines[1].contains("GitHub"))
        assertTrue(lines[1].contains("https://github.com"))
        assertTrue(lines[1].contains("user"))
        assertTrue(lines[1].contains("pass123"))
    }

    @Test
    fun `importBitwardenJson parses login items correctly`() = runTest {
        val bitwardenJson = """
            {
                "items": [
                    {
                        "type": 1,
                        "name": "Test Login",
                        "login": {
                            "uri": "https://example.com",
                            "username": "testuser",
                            "password": "testpass"
                        },
                        "notes": "Some notes"
                    }
                ]
            }
        """.trimIndent()

        val result = importExportService.importData(
            bitwardenJson,
            ImportExportService.ImportFormat.BITWARDEN_JSON
        )

        assertTrue(result is ImportExportService.ImportResult.Success)
        val success = result as ImportExportService.ImportResult.Success
        assertEquals(1, success.items.size)
        assertEquals("Test Login", success.items[0].name)
        assertEquals("login", success.items[0].type)
    }

    @Test
    fun `importChromeCsv parses correctly`() = runTest {
        val csv = "name,url,username,password\nGoogle,https://google.com,user,pass"

        val result = importExportService.importData(
            csv,
            ImportExportService.ImportFormat.CHROME_CSV
        )

        assertTrue(result is ImportExportService.ImportResult.Success)
        val success = result as ImportExportService.ImportResult.Success
        assertEquals(1, success.items.size)
        assertEquals("Google", success.items[0].name)
    }

    @Test
    fun `importLastPassCsv parses correctly`() = runTest {
        val csv = "name,url,username,password,extra\nGitHub,https://github.com,user,pass,notes"

        val result = importExportService.importData(
            csv,
            ImportExportService.ImportFormat.LASTPASS_CSV
        )

        assertTrue(result is ImportExportService.ImportResult.Success)
        val success = result as ImportExportService.ImportResult.Success
        assertEquals(1, success.items.size)
        assertEquals("GitHub", success.items[0].name)
    }

    @Test
    fun `encrypted import fails without vault key`() = runTest {
        val data = createSampleData()
        val innerJson = importExportService.exportToJson(data)

        every { cryptoManager.encryptVaultItem(any(), testVaultKey) } returns
            EncryptedBlob(
                iv = "iv".toByteArray(),
                ciphertext = innerJson.toByteArray(Charsets.UTF_8)
            )

        val encrypted = importExportService.exportToEncryptedTruvalt(data, testVaultKey)

        val result = importExportService.importData(
            encrypted,
            ImportExportService.ImportFormat.TRUVALT_ENCRYPTED,
            null
        )

        assertTrue(result is ImportExportService.ImportResult.Error)
    }

    @Test
    fun `importTruvaltJson handles empty arrays`() = runTest {
        val json = """
            {
                "version": 1,
                "items": [],
                "folders": [],
                "tags": [],
                "item_tags": []
            }
        """.trimIndent()

        val result = importExportService.importData(
            json,
            ImportExportService.ImportFormat.TRUVALT_JSON
        )

        assertTrue(result is ImportExportService.ImportResult.Success)
        val success = result as ImportExportService.ImportResult.Success
        assertEquals(0, success.items.size)
        assertEquals(0, success.folders.size)
        assertEquals(0, success.tags.size)
        assertEquals(0, success.itemTags.size)
    }
}
