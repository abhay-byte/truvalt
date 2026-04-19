package com.ivarna.truvalt.data.remote

import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class FirestoreVaultCrudTest {

    @Test
    fun liveFirebaseCrudWorksForDisposableAccount() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (FirebaseApp.getApps(context).isEmpty()) {
            assertNotNull(FirebaseApp.initializeApp(context))
        }

        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val email = "truvalt-test-${UUID.randomUUID()}@example.com"
        val password = "Test1234!${UUID.randomUUID().toString().take(8)}"
        val signInResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = requireNotNull(signInResult.user?.uid) { "Firebase auth did not return a user" }

        val repo = FirestoreVaultRepository(FirebaseFirestore.getInstance())
        val itemId = UUID.randomUUID().toString()
        val encrypted = repo.encodeData("secret-firebase".toByteArray())

        try {
            val initial = mapOf<String, Any?>(
                "id" to itemId,
                "type" to "login",
                "name" to "Firebase Login",
                "folder_id" to null,
                "favorite" to false,
                "encrypted_data" to encrypted,
                "created_at" to 1L,
                "updated_at" to 1L,
                "deleted_at" to null
            )

            repo.saveVaultItem(uid, initial)

            val created = repo.getVaultItem(uid, itemId)
            assertNotNull(created)
            assertEquals("Firebase Login", created?.get("name"))
            assertEquals(encrypted, created?.get("encrypted_data"))

            val updated = initial.toMutableMap().apply {
                put("name", "Firebase Login Updated")
                put("favorite", true)
                put("updated_at", 2L)
            }
            repo.saveVaultItem(uid, updated)

            val afterUpdate = repo.getVaultItem(uid, itemId)
            assertEquals("Firebase Login Updated", afterUpdate?.get("name"))
            assertEquals(true, afterUpdate?.get("favorite"))

            val trashed = repo.softDeleteVaultItem(uid, itemId)
            assertNotNull(trashed["deleted_at"])
            assertNull(repo.getVaultItems(uid).firstOrNull { it["id"] == itemId })

            val restored = repo.restoreVaultItem(uid, itemId)
            assertNull(restored["deleted_at"])
            assertNotNull(repo.getVaultItems(uid).firstOrNull { it["id"] == itemId })

            repo.deleteAllUserData(uid)
            assertNull(repo.getVaultItem(uid, itemId))
            assertNull(repo.getVaultItems(uid).firstOrNull { it["id"] == itemId })
        } finally {
            runCatching { repo.deleteAllUserData(uid) }
            auth.currentUser?.delete()?.await()
            auth.signOut()
        }
    }
}
