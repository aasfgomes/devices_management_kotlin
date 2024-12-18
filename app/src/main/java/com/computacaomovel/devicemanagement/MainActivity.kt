package com.computacaomovel.devicemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.computacaomovel.devicemanagement.home.*
import com.computacaomovel.devicemanagement.ui.theme.DeviceManagementTheme
import com.computacaomovel.devicemanagement.user.LoginScreen
import com.computacaomovel.devicemanagement.user.UserRegisterScreen
import com.computacaomovel.devicemanagement.user.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração do Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso).apply {
            signOut() // Força a remoção da sessão anterior para pedir sempre a conta
        }


        setContent {
            DeviceManagementTheme {
                val navController = rememberNavController()

                // Observa o estado de autenticação do utilizador
                userViewModel.isAuthenticated.observe(this, Observer { isAuthenticated ->
                    if (isAuthenticated) {
                        navController.navigate(Destino.Ecra01.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                })

                MainApp(
                    navController = navController,
                    userViewModel = userViewModel,
                    onGoogleSignIn = { signInWithGoogle() }
                )
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                val idToken = account.idToken
                if (idToken != null) {
                    userViewModel.authenticateWithGoogle(idToken) {
                        // Autenticação bem-sucedida
                    }
                }
            } else {
                userViewModel.updateResultMessage("Google Sign-In failed: ${task.exception?.message}")
            }
        }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }


    @Composable
    fun MainApp(
        navController: NavHostController,
        userViewModel: UserViewModel,
        onGoogleSignIn: () -> Unit
    ) {
        val currentRoute by navController.currentBackStackEntryAsState()

        Scaffold(
            bottomBar = {
                if (currentRoute?.destination?.route in Destino.toList.map { it.route }) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                userViewModel = userViewModel,
                onGoogleSignIn = onGoogleSignIn,
                padding = innerPadding
            )
        }
    }

    @Composable
    fun AppNavigation(
        navController: NavHostController,
        userViewModel: UserViewModel,
        onGoogleSignIn: () -> Unit,
        padding: PaddingValues
    ) {
        NavHost(navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    userViewModel = userViewModel,
                    onGoogleSignIn = onGoogleSignIn,
                    onRegister = { navController.navigate("register") },
                    onLoginSuccess = {
                        navController.navigate(Destino.Ecra01.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("register") {
                UserRegisterScreen(
                    userViewModel = userViewModel,
                    onBackToLogin = { navController.popBackStack("login", inclusive = false) }
                )
            }
            composable(Destino.Ecra01.route) { Ecra01() }
            composable(Destino.Ecra02.route) { Ecra02() }
            composable(Destino.Ecra03.route) {
                Ecra03(
                    userViewModel = userViewModel,
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        BottomNavigation {
            val currentRoute =
                navController.currentBackStackEntryAsState()?.value?.destination?.route

            Destino.toList.forEach { destino ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painterResource(id = destino.icon),
                            contentDescription = destino.title
                        )
                    },
                    label = { Text(destino.title) },
                    selected = currentRoute == destino.route,
                    onClick = {
                        navController.navigate(destino.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}