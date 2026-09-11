package com.almari.app

import android.os.Bundle
import android.content.MutableContextWrapper
import androidx.credentials.CustomCredential
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.almari.shared.data.initializeHomeStateStorage
import com.almari.shared.data.configureFirebaseSignOut
import com.almari.shared.data.handleFirebaseAuthError
import com.almari.shared.data.handleFirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private val activityScope = MainScope()
    private val credentialManager by lazy { CredentialManager.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeHomeStateStorage(this)
        configureFirebaseSignOut(::signOutFromFirebase)
        FirebaseAuth.getInstance().currentUser?.let(::publishFirebaseUser)
        enableEdgeToEdge()
        setContent { App(onNativeGoogleSignIn = ::showGoogleAccountChooser) }
    }

    override fun onDestroy() {
        configureFirebaseSignOut(null)
        activityScope.cancel()
        super.onDestroy()
    }

    private fun showGoogleAccountChooser() {
        activityScope.launch {
            try {
                val googleOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()
                val credential = try {
                    getGoogleCredential(googleOption)
                } catch (_: NoCredentialException) {
                    val signInOption = GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id)).build()
                    getGoogleCredential(signInOption)
                }
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseUser = FirebaseAuth.getInstance()
                        .signInWithCredential(GoogleAuthProvider.getCredential(googleCredential.idToken, null))
                        .await()
                        .user ?: error("Firebase did not return a signed-in user.")
                    publishFirebaseUser(firebaseUser)
                } else {
                    handleFirebaseAuthError("Google returned an unsupported credential.")
                }
            } catch (_: GetCredentialCancellationException) {
                handleFirebaseAuthError("Google sign-in was cancelled.")
            } catch (error: Exception) {
                handleFirebaseAuthError(error.message ?: "Google account selection failed.")
            }
        }
    }

    private suspend fun getGoogleCredential(option: androidx.credentials.CredentialOption) =
        credentialManager.getCredential(
            context = MutableContextWrapper(this@MainActivity),
            request = GetCredentialRequest.Builder().addCredentialOption(option).build(),
        ).credential

    private fun publishFirebaseUser(user: FirebaseUser) {
        handleFirebaseUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName.orEmpty(),
        )
    }

    private fun signOutFromFirebase() {
        FirebaseAuth.getInstance().signOut()
        activityScope.launch {
            runCatching { credentialManager.clearCredentialState(ClearCredentialStateRequest()) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    App()
}
