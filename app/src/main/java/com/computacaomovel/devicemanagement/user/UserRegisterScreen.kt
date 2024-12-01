package com.computacaomovel.devicemanagement.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.BorderStroke

/**
 * Tela de Registo de Usuário.
 * Permite que os utilizadores criem uma conta preenchendo os campos necessários.
 *
 * @param userViewModel ViewModel responsável pela lógica que envolve o user
 * @param onBackToLogin Callback para navegar de volta ao ecrã de login.
 * @param onRegister Callback para quando o registo for bem-sucedido.
 */

@Composable
fun UserRegisterScreen(
    userViewModel: UserViewModel = viewModel(), // Chama o ViewModel
    onBackToLogin: () -> Unit = {}, // Callback para voltar á página de login
    onRegister: () -> Unit = {} // Callback para quando o registo for bem-sucedido
) {
    // Variáveis para armazenar os inputs do user
    val username = remember { mutableStateOf("") } // Nome de user
    val email = remember { mutableStateOf("") } // Email
    val password = remember { mutableStateOf("") } // Password
    val confirmPassword = remember { mutableStateOf("") } // Confirmação de Password
    val result = userViewModel.result // Resultado

    // Layout principal do ecra
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
            // Logótipo
            val logo: Painter = painterResource(id = R.drawable.onis_logo)
            Image(
                painter = logo,
                contentDescription = "Logo Omatapalo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp) // Tamanho da imagem
                    .padding(bottom = 24.dp)
            )

            // Título do registo
            Text(
                text = "Cria conta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo de input para o nome de user
            OutlinedTextField(
                value = username.value, // Valor atual
                onValueChange = { username.value = it }, // Atualiza o valor
                label = { Text("Username") }, // Label do campo
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1, // Apenas 1 linha
                singleLine = true // Define que é uma única linha
            )

            // Campo de input para o email
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1,
                singleLine = true
            )

            // Campo de input para a password
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password // Teclado específico para passwords
                ),
                visualTransformation = PasswordVisualTransformation(), // Oculta a password com bolinhas
                textStyle = TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1,
                singleLine = true
            )

            // Campo de input para confirmar a password
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirmar Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão para registar o utilizador
            Button(
                onClick = {
                    if (password.value == confirmPassword.value) {
                        // Regista o user se as passwords forem iguais
                        userViewModel.registerNewUser(username.value, password.value, email.value) {
                            // Redireciona para a página de login se sucesso
                            onBackToLogin() // Chama a função para navegar para o login após o registo bem-sucedido
                        }
                    } else {
                        // Atualiza a mensagem de erro se as passwords forem diferentes
                        userViewModel.updateResultMessage("As passwords não são iguais")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Registar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White, // Texto branco
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Botão para voltar para a página de login
            OutlinedButton(
                onClick = onBackToLogin, // Callback para navegar
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Voltar para o Login",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Mensagem de resultado (sucesso ou erro)
            if (result.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp)) // Espaçamento
                Text(
                    text = result.value, // Mensagem de resultado
                    color = if (result.value.contains("successful", true))
                        MaterialTheme.colorScheme.primary // Sucesso
                    else
                        MaterialTheme.colorScheme.error, // Erro
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

