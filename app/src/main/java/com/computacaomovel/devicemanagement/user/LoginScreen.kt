package com.computacaomovel.devicemanagement.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.computacaomovel.devicemanagement.R

@Composable
fun LoginScreen(
    userViewModel: UserViewModel = viewModel(),
    onGoogleSignIn: () -> Unit,
    onRegister: () -> Unit
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val result = userViewModel.result

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo no topo
            val logo: Painter = painterResource(id = R.drawable.onis_logo) // Logo caminho
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White, CircleShape)
            ) {
                Image(
                    painter = logo,
                    contentDescription = "Logo Omatapalo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Título do Login
            Text(
                text = "Gestão de Equipamentos",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo de Username
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = {
                    Text(
                        text = "Username"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Start, // Alinha o texto à esquerda
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1,
                singleLine = true
            )

            // Campo de Password
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = {
                    Text(
                        text = "Password"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(
                    textAlign = TextAlign.Start, // Alinha o texto à esquerda
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de Login
            Button(
                onClick = {
                    userViewModel.authenticate(username.value, password.value)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Botão menor que as caixas de texto
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Entrar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Botão de Login com Google (com borda)
            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Botão menor que as caixas de texto
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Gray) // Adiciona uma borda cinza ao botão
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google), // Icon google
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continuar com Google",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Botão de Registo
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Botão menor que as caixas de texto
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Criar Conta",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Mensagem de resultado
            if (result.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = result.value,
                    color = if (result.value.contains("successful", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
