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

/**
 * - Surface com fillMaxSize: O ecrã preenche o espaço disponivel.
 * - Column com padding horizontal e alinhamentos: A coluna centraliza os elementos e adiciona margens laterais.
 * - Image (Logótipo): Exibido com tamanho fixo e espaçamento inferior.
 * - Text (Título): Define o título "Cria conta" com tipografia e peso de fonte em negrito.
 * - OutlinedTextField (Username, Email, Password, Confirmar Password): Campos de input com largura total, padding vertical e cantos arredondados.
 * - PasswordVisualTransformation: Oculta o texto dos campos de password.
 * - Button (Registar): Botão principal para registo, ocupa 70% da largura disponível.
 * - OutlinedButton (Voltar para Login): Botão para navegar de volta, com uma borda visível.
 * - Mensagem de resultado: Exibe mensagens de sucesso ou erro após tentativas de registo.
 * - fillMaxSize, fillMaxWidth: Usados para garantir que os elementos ocupam o máximo espaço disponível.
 * - padding: Aplicado para adicionar espaçamento entre elementos e melhorar a aparência visual.
 * - RoundedCornerShape: Define cantos arredondados para os campos e botões.
 * - textStyle, fontWeight, fontSize: Aplicados para configurar a aparência do texto, como alinhamento, tamanho e peso.
 * - buttonColors, border: Configuram as cores dos botões e as bordas.
 */

@Composable
fun LoginScreen(
    userViewModel: UserViewModel = viewModel(),
    onGoogleSignIn: () -> Unit,
    onRegister: () -> Unit,
    onLoginSuccess: () -> Unit
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
            val logo: Painter = painterResource(id = R.drawable.onis_logo)
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
                label = { Text(text = "Username") },
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

            // Campo de Password
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = "Password") },
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

            // Botão de Login
            Button(
                onClick = {
                    userViewModel.authenticate(username.value, password.value) {
                        onLoginSuccess() // Chama a função onLoginSuccess após login bem-sucedido
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
                    text = "Entrar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Botão Login Google
            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
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

            // Botão para fazer o registo
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
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
