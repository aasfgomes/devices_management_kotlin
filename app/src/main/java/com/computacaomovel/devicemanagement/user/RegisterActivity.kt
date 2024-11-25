package com.computacaomovel.devicemanagement.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.computacaomovel.devicemanagement.ui.theme.DeviceManagementTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceManagementTheme {
                RegisterScreen(
                    userViewModel = UserViewModel(),
                    onBackToLogin = { finish() } // Finaliza a atividade e volta ao ecrã anterior (Login)
                )
            }
        }
    }
}
