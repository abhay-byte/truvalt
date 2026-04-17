package com.ivarna.truvalt.autofill

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.domain.model.LoginItemData
import com.ivarna.truvalt.domain.repository.SyncRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AutofillSaveActivity : AppCompatActivity() {

    @Inject
    lateinit var vaultItemDao: VaultItemDao

    @Inject
    lateinit var vaultKeyManager: VaultKeyManager

    @Inject
    lateinit var cryptoManager: CryptoManager

    @Inject
    lateinit var syncRepository: SyncRepository

    private val json = Json { ignoreUnknownKeys = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val username = intent.getStringExtra("username") ?: return finish()
        val password = intent.getStringExtra("password") ?: return finish()
        val url = intent.getStringExtra("url") ?: ""

        setContent {
            var name by remember { mutableStateOf(extractNameFromUrl(url)) }
            var showPassword by remember { mutableStateOf(false) }
            var isSaving by remember { mutableStateOf(false) }
            var saveError by remember { mutableStateOf<String?>(null) }

            Scaffold { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Save to Truvalt?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = {},
                        label = { Text("Username/Email") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {},
                        label = { Text("Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { showPassword = !showPassword }) {
                                Text(if (showPassword) "Hide" else "Show")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = url,
                        onValueChange = {},
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    saveError?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { finish() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                isSaving = true
                                saveCredentials(name, username, password, url) { error ->
                                    isSaving = false
                                    saveError = error
                                    if (error == null) finish()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving && name.isNotBlank()
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveCredentials(
        name: String,
        username: String,
        password: String,
        url: String,
        onComplete: (String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val vaultKey = vaultKeyManager.getInMemoryKey()
                    ?: return@launch onComplete("Vault is locked")

                val loginItem = LoginItemData(
                    url = url,
                    username = username,
                    password = password,
                    totpSeed = null,
                    notes = "",
                    customFields = emptyList()
                )

                val jsonData = json.encodeToString(loginItem)
                val encryptedBlob = cryptoManager.encryptVaultItem(jsonData.toByteArray(), vaultKey)
                
                // Combine iv + ciphertext for storage
                val encryptedData = encryptedBlob.iv + encryptedBlob.ciphertext

                val now = System.currentTimeMillis()
                val entity = VaultItemEntity(
                    id = UUID.randomUUID().toString(),
                    type = "login",
                    name = name,
                    folderId = null,
                    encryptedData = encryptedData,
                    favorite = false,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null,
                    syncStatus = "pending"
                )

                vaultItemDao.insertItem(entity)
                syncRepository.sync()

                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onComplete(e.message)
                }
            }
        }
    }

    private fun extractNameFromUrl(url: String): String {
        return try {
            val domain = url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
            val slashIndex = domain.indexOf('/')
            if (slashIndex > 0) domain.substring(0, slashIndex) else domain
        } catch (e: Exception) {
            url
        }
    }
}
