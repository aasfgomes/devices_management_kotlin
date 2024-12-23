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
            signOut() // Força a sair para pedir sempre a conta Google
        }

        setContent {
            DeviceManagementTheme {
                val navController = rememberNavController()

                // Observa o estado de autenticação e dados do utilizador
                val isAuthenticated = userViewModel.isAuthenticated.observeAsState(initial = false).value
                val userData = userViewModel.userData.observeAsState().value

                LaunchedEffect(isAuthenticated, userData) {
                    if (isAuthenticated && userData != null) {
                        navController.navigate(Destino.Ecra01.route) {
                            popUpTo("login") { inclusive = true }
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
                    userViewModel.authenticateWithGoogle(idToken) { }
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

        val destinos = if (userType == "user") {
            listOf(Destino.Ecra01, Destino.Ecra03)
        } else {
            Destino.toList
        }

        Scaffold(
            bottomBar = {
                val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
                if (userData != null && currentRoute != null && destinos.any { it.route == currentRoute }) {
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
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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
                            launchSingleTop = true
                            restoreState = true
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
        val userData = userViewModel.userData.observeAsState().value
        val userType = userData?.type

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

            composable(Destino.Ecra01.route) {
                Ecra01(
                    deviceViewModel = deviceViewModel,
                    navController = navController,
                    onAddDeviceClick = { navController.navigate("addDevice") }
                )
            }

            if (userType == "user") {
                composable(Destino.Ecra03.route) {
                    Ecra03(
                        userViewModel = userViewModel,
                        onLogout = {
                            userViewModel.logout()
                            navController.navigate("login") { popUpTo("login") { inclusive = true } }
                        }
                    )
                }
            } else {
                composable(Destino.Ecra02.route) {
                    Ecra02(deviceViewModel = deviceViewModel)
                }

                composable(Destino.Ecra03.route) {
                    Ecra03(
                        userViewModel = userViewModel,
                        onLogout = {
                            userViewModel.logout()
                            navController.navigate("login") { popUpTo("login") { inclusive = true } }
                        }
                    )
                }
            }

            composable("register") {
                UserRegisterScreen(
                    userViewModel = userViewModel,
                    onBackToLogin = { navController.popBackStack("login", inclusive = false) }
                )
            }

            composable("addDevice") {
                EcraAddDevice(
                    deviceViewModel = deviceViewModel,
                    onDeviceAdded = { navController.popBackStack() },
                    onDeviceUpdated = {},
                    onBack = { navController.popBackStack() }
                )
            }

            composable("deviceInformation/{deviceId}") { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId")
                EcraInformation(
                    deviceId = deviceId,
                    deviceViewModel = deviceViewModel,
                    onBack = { navController.popBackStack() },
                    onDeleteDevice = {
                        if (deviceId != null) {
                            deviceViewModel.deleteDevice(deviceId) { success ->
                                if (success) navController.popBackStack()
                            }
                        }
                    },
                    onUpdateDevice = {
                        if (deviceId != null) {
                            navController.navigate("editDevice/$deviceId")
                        }
                    }
                )
            }

            composable("editDevice/{deviceId}") { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId")
                val initialDeviceData = deviceViewModel.deviceList.value?.find { it["uid"].toString() == deviceId }

                EcraAddDevice(
                    deviceViewModel = deviceViewModel,
                    onDeviceAdded = { navController.popBackStack() },
                    onDeviceUpdated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    isEditing = true,
                    initialDeviceData = initialDeviceData
                )
            }
        }
    }
}
