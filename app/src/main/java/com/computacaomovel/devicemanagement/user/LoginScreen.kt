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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(userViewModel: UserViewModel = viewModel(), onGoogleSignIn: () -> Unit, onRegister: () -> Unit) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val result = userViewModel.result

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(0.85f),
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(0.85f),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardOptions.Default.keyboardType
            ),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            userViewModel.authenticate(username.value, password.value)
        }, modifier = Modifier.fillMaxWidth(0.75f)) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onGoogleSignIn, modifier = Modifier.fillMaxWidth(0.75f)) {
            Text("Sign in with Google")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRegister, modifier = Modifier.fillMaxWidth(0.75f)) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (result.value.isNotEmpty()) {
            Text(
                text = result.value,
                color = if (result.value.contains("successful", true)) Color.Black else Color.Red,
                fontSize = 14.sp
            )
        }
    }
}
