package com.ivarna.truvalt.data.remote

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface FirebaseSessionProvider {
    fun currentUserUid(): String?
}

@Singleton
class FirebaseAuthSessionProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : FirebaseSessionProvider {
    override fun currentUserUid(): String? = firebaseAuth.currentUser?.uid
}
