package com.ivarna.truvalt.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.FirestoreVaultRepository
import com.ivarna.truvalt.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
    private val preferences: TruvaltPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreRepository: FirestoreVaultRepository,
) : AuthRepository {

    private var masterKey: ByteArray? = null

    override suspend fun isVaultUnlocked(): Boolean {
        return masterKey != null && preferences.isVaultUnlocked.first()
    }

    override suspend fun unlockVault(masterKey: ByteArray) {
        this.masterKey = masterKey
        preferences.setVaultUnlocked(true)
    }

    override suspend fun lockVault() {
        masterKey?.fill(0)
        masterKey = null
        preferences.setVaultUnlocked(false)
    }

    override suspend fun isBiometricEnabled(): Boolean {
        return preferences.isBiometricEnabledSync()
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        preferences.setBiometricEnabled(enabled)
    }

    override suspend fun getEncryptedVaultKey(): ByteArray? {
        val encryptedKey = preferences.encryptedVaultKey.first() ?: return null
        return android.util.Base64.decode(encryptedKey, android.util.Base64.DEFAULT)
    }

    override suspend fun storeVaultKey(encryptedKey: ByteArray) {
        val encoded = android.util.Base64.encodeToString(encryptedKey, android.util.Base64.DEFAULT)
        preferences.setEncryptedVaultKey(encoded)
    }

    override suspend fun hasVault(): Boolean {
        return if (preferences.isLocalOnlySync()) {
            preferences.encryptedVaultKey.first() != null
        } else {
            // In cloud mode, vault exists when a Firebase user is signed in
            firebaseAuth.currentUser != null
        }
    }

    override suspend fun createVault(email: String, password: String): Result<Unit> {
        return try {
            // Derive encryption keys from master password
            val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
            masterKey = derivedKeys.masterKey

            if (!preferences.isLocalOnlySync()) {
                // Direct Firebase Auth registration
                val result = firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .await()
                val firebaseUser = result.user
                    ?: return Result.failure(IllegalStateException("Firebase registration returned no user"))

                // Store Firebase session info
                val idToken = firebaseUser.getIdToken(false).await().token ?: ""
                preferences.setBackendUserId(firebaseUser.uid)
                preferences.setBackendIdToken(idToken)
                preferences.setBackendRefreshToken(null)

                // Bootstrap Firestore user profile
                firestoreRepository.upsertUserProfile(
                    uid = firebaseUser.uid,
                    email = email,
                    provider = "password",
                )
            }

            val encryptedVaultKey = cryptoManager.encryptWithKeystore(derivedKeys.vaultKey)
            storeVaultKey(encryptedVaultKey)

            preferences.setUserEmail(email)
            preferences.setAuthKeyHash(cryptoManager.hashAuthKey(derivedKeys.authKey))
            preferences.setVaultUnlocked(true)

            Result.success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(IllegalStateException("An account with this email already exists"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlockWithPassword(email: String, password: String): Result<Unit> {
        return try {
            val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
            masterKey = derivedKeys.masterKey

            if (!preferences.isLocalOnlySync()) {
                val result = firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .await()
                val firebaseUser = result.user
                    ?: return Result.failure(IllegalStateException("Firebase sign-in returned no user"))

                val idToken = firebaseUser.getIdToken(true).await().token ?: ""
                preferences.setBackendUserId(firebaseUser.uid)
                preferences.setBackendIdToken(idToken)
                preferences.setBackendRefreshToken(null)

                firestoreRepository.upsertUserProfile(
                    uid = firebaseUser.uid,
                    email = email,
                    provider = "password",
                )
            }

            preferences.setUserEmail(email)
            preferences.setAuthKeyHash(cryptoManager.hashAuthKey(derivedKeys.authKey))
            preferences.setVaultUnlocked(true)

            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(IllegalStateException("Invalid email or password"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(IllegalStateException("No account found with this email"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in / sign up via Google ID token (from Credential Manager).
     * For Google accounts the vault key is derived from the Firebase UID
     * (since there is no user-supplied master password in this flow).
     */
    suspend fun signInWithGoogle(googleIdToken: String): Result<Unit> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(googleIdToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("Google sign-in returned no user"))

            val email = firebaseUser.email ?: firebaseUser.uid
            val idToken = firebaseUser.getIdToken(true).await().token ?: ""

            preferences.setBackendUserId(firebaseUser.uid)
            preferences.setBackendIdToken(idToken)
            preferences.setBackendRefreshToken(null)
            preferences.setUserEmail(email)

            // For Google accounts: derive vault key from UID (deterministic, no master password)
            val derivedKeys = cryptoManager.deriveKeyFromPassword(firebaseUser.uid, email)
            masterKey = derivedKeys.masterKey
            preferences.setAuthKeyHash(cryptoManager.hashAuthKey(derivedKeys.authKey))

            val encryptedVaultKey = cryptoManager.encryptWithKeystore(derivedKeys.vaultKey)
            storeVaultKey(encryptedVaultKey)

            firestoreRepository.upsertUserProfile(
                uid = firebaseUser.uid,
                email = email,
                provider = "google.com",
            )

            preferences.setVaultUnlocked(true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Returns the current Firebase UID, or null if not signed in. */
    fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    /** Returns the in-memory master key (null when vault is locked). */
    fun getMasterKey(): ByteArray? = masterKey

    /**
     * Re-derives the vault key from email + password.
     * Only called when the in-memory key is needed but masterKey is null
     * (e.g. when re-encrypting after unlock).
     */
    fun getVaultKey(email: String, password: String): ByteArray {
        val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
        return derivedKeys.vaultKey
    }
}
