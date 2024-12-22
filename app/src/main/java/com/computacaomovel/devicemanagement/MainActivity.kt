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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.computacaomovel.devicemanagement.device.DeviceViewModel
import com.computacaomovel.devicemanagement.ui.theme.DeviceManagementTheme
import com.computacaomovel.devicemanagement.screen.LoginScreen
import com.computacaomovel.devicemanagement.screen.UserRegisterScreen
import com.computacaomovel.devicemanagement.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.computacaomovel.devicemanagement.screen.Ecra01
import com.computacaomovel.devicemanagement.screen.Ecra02
import com.computacaomovel.devicemanagement.screen.Ecra03
import com.computacaomovel.devicemanagement.screen.EcraAddDevice
import com.computacaomovel.devicemanagement.screen.EcraInformation

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private val deviceViewModel: DeviceViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração do Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso).apply {
            signOut() // Força a sair para pedir sempre a conta google
        }

        setContent {
            DeviceManagementTheme {
                val navController = rememberNavController()

                // Observa o estado de autenticação do utilizador
                userViewModel.isAuthenticated.observe(this) { isAuthenticated ->
                    if (isAuthenticated) {
                        navController.navigate(Destino.Ecra01.route) {
                            popUpTo("login") {
                                inclusive = true
                            } // Limpa o histórico ao navegar para o Home
                        }
                    }
                }

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
        val userData = userViewModel.userData.observeAsState().value
        val userType = userData?.type
        val isAuthenticated = userViewModel.isAuthenticated.observeAsState(initial = false).value

        // Define os destinos dinamicamente com base no tipo de utilizador
        val destinos = remember(userType) {
            if (userType == "user") {
                listOf(
                    Destino.Ecra01, // Home
                    Destino.Ecra03  // Perfil
                )
            } else {
                Destino.toList // Para admin ou outros tipos de utilizadores
            }
        }

        // Lógica para navegação inicial baseada no estado de autenticação
        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated) {
                navController.navigate(Destino.Ecra01.route) {
                    popUpTo("login") { inclusive = true } // Redireciona para Home se autenticado
                }
            } else {
                navController.navigate("login") {
                    popUpTo(0) // Limpa toda a pilha de navegação ao redirecionar para login
                }
            }
        }

        Scaffold(
            bottomBar = {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                // Mostra a barra de navegação apenas se autenticado e a rota atual for válida
                if (isAuthenticated && destinos.any { it.route == currentRoute }) {
                    BottomNavigationBar(navController, destinos)
                }
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                userViewModel = userViewModel,
                deviceViewModel = deviceViewModel,
                onGoogleSignIn = onGoogleSignIn,
                padding = innerPadding
            )
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController, destinos: List<Destino>) {
        BottomNavigation {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route

            destinos.forEach { destino ->
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
                            launchSingleTop = true // Garante que só existe uma instância
                            restoreState = true // Restaura o estado ao navegar novamente
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun AppNavigation(
        navController: NavHostController,
        userViewModel: UserViewModel,
        deviceViewModel: DeviceViewModel,
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
            composable(Destino.Ecra01.route) {
                Ecra01(
                    deviceViewModel = deviceViewModel,
                    navController = navController, // Passa o navController aqui
                    onAddDeviceClick = { navController.navigate("addDevice") }
                )
            }
            composable(Destino.Ecra02.route) { Ecra02() }
            composable(Destino.Ecra03.route) {
                Ecra03(
                    userViewModel = userViewModel,
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate("login") { popUpTo("login") { inclusive = true } }
                    }
                )
            }
            composable("addDevice") {
                EcraAddDevice(
                    deviceViewModel = deviceViewModel,
                    onDeviceAdded = { navController.popBackStack() }, // Voltar após adicionar
                    //onDeviceUpdated = { navController.popBackStack() }, // Voltar após atualizar
                    onBack = { navController.popBackStack() }
                )
            }

            // Navegação para EcraInformation
            composable("deviceInformation/{deviceId}") { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId")
                EcraInformation(
                    deviceId = deviceId,
                    deviceViewModel = deviceViewModel,
                    onBack = { navController.popBackStack() }, // Voltar
                    onDeleteDevice = {
                        if (deviceId != null) {
                            deviceViewModel.deleteDevice(deviceId) { success ->
                                if (success) navController.popBackStack() // Volta ao ecrã anterior
                            }
                        }
                    }
                )
            }

            composable("editDevice/{deviceId}") { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId")
                EcraAddDevice(
                    deviceViewModel = deviceViewModel,
                    onDeviceAdded = { navController.popBackStack() }, // Voltar após adicionar
                    //onDeviceUpdated = { navController.popBackStack() }, // Voltar após atualizar
                    onBack = { navController.popBackStack() },
                    //isEditing = true, // Modo de edição
                    //initialDeviceData = deviceViewModel.deviceList.value?.find { it["uid"].toString() == deviceId }
                )
            }

        }
    }
}
