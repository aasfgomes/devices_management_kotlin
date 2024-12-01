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


//
// - .fillMaxSize(): Faz com que o elemento ocupe o espaco que existe.
// - .fillMaxWidth(): Faz com que o elemento ocupe toda a largura disponível.
// - .padding(): Adiciona espaçamento á volta de um elemento.
// - .align(): Alinha o elemento dentro de um container .
// - .size(): Define o tamanho fixo de um elemento.
// - .background(): Define a cor ou forma de fundo de um elemento.
// - Spacer(): Insere um espaço vazio entre elementos que é usado para separação.
// - RoundedCornerShape(): Define bordas arredondadas para botões ou caixas.
// - ButtonDefaults.buttonColors(): Permite personalizar as cores dos botões.
// - OutlinedTextField(): Campo de texto com borda e rótulo acima.
// - PasswordVisualTransformation(): Esconde os caracteres escritos e ficam em formatos de bolinhas (para campos de senha).
// - Surface(): Container que suporta cores e temas do Material Design.
// - Column(): Layout que organiza elementos verticalmente, um em baixo do outro.
//

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

            Button(
                onClick = {
                    userViewModel.authenticate(username.value, password.value) {
                        // Aqui você pode definir o comportamento ao autenticar com sucesso
                        // Exemplo: redirecionar para a tela principal ou uma ação específica
                    }
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

            // Botão Login Google
            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Botão fica mais pequeno que as caixas de texto
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

            // Botão para fazer o registo
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Botão fica mais pequeno que as caixas de texto
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
