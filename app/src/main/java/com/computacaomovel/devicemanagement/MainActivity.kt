package com.computacaomovel.devicemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import com.computacaomovel.devicemanagement.user.*
import com.computacaomovel.devicemanagement.home.HomeScreen
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

        // Observar o estado de autenticação
        userViewModel.isAuthenticated.observe(this, Observer { isAuthenticated ->
            // Após a autenticação bem-sucedida, renderizar o HomeScreen
            if (isAuthenticated) {
                setContent {
                    DeviceManagementTheme {
                        HomeScreen() // Mostrar HomeScreen quando autenticado
                    }
                }
            }
        })

        setContent {
            DeviceManagementTheme {
                var showRegisterScreen by remember { mutableStateOf(false) }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (showRegisterScreen) {
                        UserRegisterScreen(
                            userViewModel = userViewModel,
                            onBackToLogin = { showRegisterScreen = false }
                        )
                    } else {
                        LoginScreen(
                            userViewModel = userViewModel,
                            onGoogleSignIn = {
                                signInWithGoogle()
                            },
                            onRegister = {
                                showRegisterScreen = true
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
