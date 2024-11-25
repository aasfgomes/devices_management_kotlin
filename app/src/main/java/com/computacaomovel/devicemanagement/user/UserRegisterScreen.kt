package com.computacaomovel.devicemanagement.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserRegisterScreen(userViewModel: UserViewModel = viewModel(), onBackToLogin: () -> Unit = {}) {
    // Estados para guardar os valores dos campos de username, password e email
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val result = userViewModel.result

    // Container principal para o ecrã de registo
    Column(
        modifier = Modifier
            .fillMaxSize() // Preenche o tamanho do ecrã
            .padding(16.dp), // Adiciona um padding ao conteúdo
        horizontalAlignment = Alignment.CenterHorizontally, // Centra o conteúdo horizontalmente
        verticalArrangement = Arrangement.Center // Posiciona o conteúdo ao centro de forma vertical
    ) {
        // Título do ecrã de registo
        Text(
            text = "Registo",
            style = TextStyle(fontSize = 32.sp, textAlign = TextAlign.Center),
            modifier = Modifier.padding(bottom = 24.dp) // Adiciona espaçamento abaixo do título
        )

        // Campo do username
        OutlinedTextField(
            value = username.value, // Valor atual do campo de username
            onValueChange = { username.value = it }, // Atualiza o estado quando o valor muda
            label = { Text(text = "Username") }, // Tag para o campo de texto
            modifier = Modifier.fillMaxWidth(0.85f), // Preenche 85% da largura
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
            maxLines = 1 // Limita a entrada a uma linha no campo
        )
        Spacer(modifier = Modifier.height(16.dp)) // Adiciona espaço entre os campos

        // Campo do email
        OutlinedTextField(
            value = email.value, // Valor atual do campo de email
            onValueChange = { email.value = it }, // Atualiza o estado quando o valor muda
            label = { Text(text = "Email") }, // Tag para o campo de texto
            modifier = Modifier.fillMaxWidth(0.85f), // Preenche 85% da largura
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
            maxLines = 1 // Limita a entrada a uma linha no campo
        )
        Spacer(modifier = Modifier.height(16.dp)) // Adiciona espaço entre os campos

        // Campo de entrada para a password
        OutlinedTextField(
            value = password.value, // Valor atual do campo de password
            onValueChange = { password.value = it }, // Atualiza o estado quando o valor muda
            label = { Text(text = "Password") }, // Tag para o campo de texto
            modifier = Modifier.fillMaxWidth(0.85f), // Preenche 85% da largura
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password // Define o tipo de teclado para a password
            ),
            visualTransformation = PasswordVisualTransformation(), // Oculta a senha com bolinhas
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
            maxLines = 1 // Limita a entrada a uma linha
        )

        Spacer(modifier = Modifier.height(16.dp)) // Adiciona espaço entre o campo de password e o botão de registo

        // Botão de registo
        Button(
            onClick = {
                userViewModel.registerNewUser(username.value, password.value, email.value) // Chama a função de registo do userViewModel
            },
            modifier = Modifier.fillMaxWidth(0.75f) // Preenche 75% da largura
        ) {
            Text("Registar") // Texto dentro do botão
        }
        Spacer(modifier = Modifier.height(16.dp)) // Adiciona espaço entre o botão de registo e o botão de voltar

        // Botão de voltar para a página de login
        Button(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth(0.75f) // Preenche 75% da largura
        ) {
            Text("Voltar para o Login") // Texto dentro do botão
        }

        Spacer(modifier = Modifier.height(16.dp)) // Adiciona espaço entre o botão e a mensagem de resultado

        // Exibe a mensagem de resultado (ex: sucesso ou não sucesso)
        if (result.value.isNotEmpty()) {
            Text(
                text = result.value, // Mostra a mensagem de resultado
                color = if (result.value.contains("successful", true)) Color.Black else Color.Red, // Altera a cor com base na mensagem
                fontSize = 14.sp // Define o tamanho da fonte para 14sp
            )
        }
    }
}
