package com.computacaomovel.devicemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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
import com.computacaomovel.devicemanagement.user.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : ComponentActivity() {

    // ViewModel que gere o estado do user
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Config das opções de auth da Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            DeviceManagementTheme {
                val navController = rememberNavController()
                var isUserAuthenticated by remember { mutableStateOf(false) }

                // Observa o estado de autenticação do utilizador e atualiza a variável
                userViewModel.isAuthenticated.observe(this, Observer { isAuthenticated ->
                    isUserAuthenticated = isAuthenticated
                })

                // Define a UI principal da aplicação
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isUserAuthenticated) "home" else "login"
                    ) {
                        // Ecrã Login
                        composable("login") {
                            LoginScreen(
                                userViewModel = userViewModel,
                                onGoogleSignIn = {
                                    signInWithGoogle()
                                },
                                onRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        // Ecrã registo
                        composable("register") {
                            UserRegisterScreen(
                                userViewModel = userViewModel,
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        // Ecrã para onde vai depois de estar autenticado - Ecrã01 / Lista
                        composable("home") {
                            ProgramaPrincipal(navController)
                        }
                    }
                }
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
                userViewModel.authenticateWithGoogle(idToken)
            }
        } else {
            userViewModel.updateResultMessage("Google Sign-In failed: \${task.exception?.message}")
        }
    }

    // Processo de autenticação do Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}

// Função principal da aplicação que mostra a barra de navegação inferior
@Composable
fun ProgramaPrincipal(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, appItems = Destino.toList) },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AppNavigation(navController = navController)
            }
        }
    )
}

// Função que define a navegação entre os diferentes ecrãs da aplicação
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Destino.Ecra01.route) {
        composable(Destino.Ecra01.route) { Ecra01() }
        composable(Destino.Ecra02.route) { Ecra02() }
        composable(Destino.Ecra03.route) { Ecra03() }
        composable(Destino.Ecra04.route) { Ecra04() }
        composable(Destino.Ecra05.route) { Ecra05() }
    }
}

// Função que cria a barra de navegação inferior para navegar entre os ecrãs
@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<Destino>) {
    BottomNavigation(backgroundColor = colorResource(id = R.color.purple_700), contentColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        appItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color.White else Color.White.copy(0.4F)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (currentRoute == item.route) Color.White else Color.White.copy(0.4F)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route -> popUpTo(route) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
