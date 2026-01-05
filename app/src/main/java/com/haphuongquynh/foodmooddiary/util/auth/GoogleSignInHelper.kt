package com.haphuongquynh.foodmooddiary.util.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.haphuongquynh.foodmooddiary.R
import kotlinx.coroutines.tasks.await

/**
 * Helper class for Google Sign-In using One Tap Sign-In
 * This provides a more streamlined sign-in experience
 */
class GoogleSignInHelper(
    private val context: Context
) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    
    private val signInRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Get web client ID from google-services.json
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    // Show all accounts, not just those previously used
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    /**
     * Begin the Google Sign-In flow
     * @param launcher ActivityResultLauncher to handle the result
     */
    suspend fun beginSignIn(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        try {
            val result = oneTapClient.beginSignIn(signInRequest).await()
            val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
            launcher.launch(intentSenderRequest)
        } catch (e: Exception) {
            // If One Tap fails, we could fall back to traditional sign-in
            throw e
        }
    }

    /**
     * Get the ID token from the sign-in result
     * @param data Intent data from the activity result
     * @return Google ID token for Firebase authentication
     */
    fun getSignInCredentialFromIntent(data: Intent?): String? {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            credential.googleIdToken
        } catch (e: ApiException) {
            null
        }
    }

    /**
     * Sign out from Google
     */
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
        } catch (e: Exception) {
            // Ignore sign-out errors
        }
    }
}
