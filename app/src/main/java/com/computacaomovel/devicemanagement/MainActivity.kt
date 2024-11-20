package com.computacaomovel.devicemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.computacaomovel.devicemanagement.ui.theme.DeviceManagementTheme
import com.computacaomovel.devicemanagement.user.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceManagementTheme {
                LoginScreen()
            }
        }
    }
}
