package com.computacaomovel.devicemanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import com.computacaomovel.devicemanagement.user.LoginScreen
import com.computacaomovel.devicemanagement.user.UserViewModel
import com.computacaomovel.devicemanagement.ui.theme.DeviceManagementTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar as opções do Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Certifica-te de que `default_web_client_id` está no ficheiro strings.xml
            .requestEmail()
            .build()

        // Inicializar GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar o ecrã de login
        setContent {
            DeviceManagementTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LoginScreen(
                        userViewModel = userViewModel,
                        onGoogleSignIn = {
                            signInWithGoogle()
                        }
                    )
                }
            }
        }
    }

    // Função para iniciar o SignIn com Google
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
            userViewModel.setResult("Google Sign-In failed: ${task.exception?.message}")
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}
