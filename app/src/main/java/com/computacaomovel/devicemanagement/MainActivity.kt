package com.computacaomovel.devicemanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.computacaomovel.devicemanagement.home.HomeScreen
import com.computacaomovel.devicemanagement.user.*
import com.computacaomovel.devicemanagement.ui.theme.DeviceManagementTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            DeviceManagementTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (userViewModel.isAuthenticated.value == true) {
                        HomeScreen() // Mostra o HomeScreen após autenticação
                    } else {
                        LoginScreen(
                            userViewModel = userViewModel,
                            onGoogleSignIn = {
                                signInWithGoogle()
                            },
                            onRegister = {
                                val intent = Intent(this, RegisterActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            val account = task.result
            val idToken = account.idToken
            if (idToken != null) {
                userViewModel.authenticateWithGoogle(idToken)
            }
        } else {
            userViewModel.updateResultMessage("Google Sign-In failed: ${task.exception?.message}")
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}
