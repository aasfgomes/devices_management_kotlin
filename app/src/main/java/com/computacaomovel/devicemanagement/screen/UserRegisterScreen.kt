package com.computacaomovel.devicemanagement.screen

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
import com.computacaomovel.devicemanagement.viewmodel.UserViewModel

/**
 * ecrã de registo dos user
 * Permite que os users criem uma conta.
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

    // Variáveis para mensagens de erro
    val usernameError = remember { mutableStateOf<String?>(null) }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }
    val confirmPasswordError = remember { mutableStateOf<String?>(null) }

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
                onValueChange = {
                    username.value = it
                    usernameError.value = null
                }, // Atualiza o valor
                label = { Text("Username") }, // Label do campo
                isError = usernameError.value != null, // Define estado de erro
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
            usernameError.value?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            // Campo de input para o email
            OutlinedTextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                    emailError.value = null
                },
                label = { Text("Email") },
                isError = emailError.value != null,
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
            emailError.value?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            // Campo de input para a password
            OutlinedTextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                    passwordError.value = null
                },
                label = { Text("Password") },
                isError = passwordError.value != null,
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
            passwordError.value?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            // Campo de input para confirmar a password
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = {
                    confirmPassword.value = it
                    confirmPasswordError.value = null
                },
                label = { Text("Confirmar Password") },
                isError = confirmPasswordError.value != null,
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
            confirmPasswordError.value?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão para registar o user
            Button(
                onClick = {
                    var isValid = true
                    if (username.value.isBlank()) {
                        usernameError.value = "O campo 'Username' é obrigatório"
                        isValid = false
                    }
                    if (email.value.isBlank()) {
                        emailError.value = "O campo 'Email' é obrigatório"
                        isValid = false
                    }
                    if (password.value.isBlank()) {
                        passwordError.value = "O campo 'Password' é obrigatório"
                        isValid = false
                    }
                    if (confirmPassword.value.isBlank()) {
                        confirmPasswordError.value = "O campo 'Confirmar Password' é obrigatório"
                        isValid = false
                    }
                    if (password.value != confirmPassword.value) {
                        confirmPasswordError.value = "As passwords não são iguais"
                        isValid = false
                    }

                    if (isValid) {
                        // Regista o user se todos os campos forem válidos
                        userViewModel.registerNewUser(username.value, password.value, email.value) {
                            // Redireciona para a página de login em caso de sucesso
                            onBackToLogin()
                        }
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
