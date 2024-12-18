package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.viewmodel.UserViewModel

@Composable
fun Ecra03(
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        userViewModel.getCurrentUserData()
    }

    val userData = userViewModel.userData.value ?: UserViewModel.UserData("", "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Perfil do Utilizador",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Username: ${userData.username}", fontSize = 16.sp)
        Text(text = "Email: ${userData.email}", fontSize = 16.sp)
        Text(text = "Type: ${userData.type}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "Logout",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
