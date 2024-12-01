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
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Observer
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

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            DeviceManagementTheme {
                val navController = rememberNavController()

                // Observa o estado de autenticação do user
                userViewModel.isAuthenticated.observe(this, Observer { isAuthenticated ->
                    if (isAuthenticated) {
                        // Vai para o Ecra 1 ao ser autenticado com sucesso
                        navController.navigate(Destino.Ecra01.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                })

                MainApp(
                    navController = navController,
                    onGoogleSignIn = { signInWithGoogle() }
                )
            }
        }
    }

    // Gere a atividade de autenticação da Google
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            val account = task.result
            val idToken = account.idToken
            if (idToken != null) {
                userViewModel.authenticateWithGoogle(idToken) {
                    // Sucesso na autenticação com Google
                }
            }
        } else {
            userViewModel.updateResultMessage("Google Sign-In failed: \${task.exception?.message}")
        }
    }

    // Processo de autenticação da Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}

@Composable
fun MainApp(
    navController: NavHostController,
    onGoogleSignIn: () -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Exibe a barra de navegação apenas se a rota não for "login" ou "register"
            if (currentRoute in Destino.toList.map { it.route }) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavigation(navController = navController, onGoogleSignIn = onGoogleSignIn, padding = innerPadding, onLoginSuccess = {
            // Redireciona para o Ecra01 ao autenticar com sucesso
            navController.navigate(Destino.Ecra01.route) {
                popUpTo("login") { inclusive = true }
            }
        })
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onGoogleSignIn: () -> Unit,
    padding: PaddingValues,
    onLoginSuccess: () -> Unit
) {
    NavHost(navController, startDestination = "login") {
        // Ecra de Login
        composable("login") {
            LoginScreen(
                userViewModel = UserViewModel(),
                onGoogleSignIn = onGoogleSignIn,
                onRegister = { navController.navigate("register") },
                onLoginSuccess = onLoginSuccess
            )
        }
        // Ecra de registo
        composable("register") {
            UserRegisterScreen(
                userViewModel = UserViewModel(),
                onBackToLogin = { navController.popBackStack("login", inclusive = false) }
            )
        }
        // Ecras com navegação
        composable(Destino.Ecra01.route) { Ecra01() }
        composable(Destino.Ecra02.route) { Ecra02() }
        composable(Destino.Ecra03.route) { Ecra03() }
        composable(Destino.Ecra04.route) { Ecra04() }
        composable(Destino.Ecra05.route) { Ecra05() }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation {
        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

        Destino.toList.forEach { destino ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = destino.icon), contentDescription = destino.title) },
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
