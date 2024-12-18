package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.device.DeviceViewModel
import kotlinx.coroutines.launch

@Composable
fun Ecra01(deviceViewModel: DeviceViewModel) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Teste de Criação de Dispositivo",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    deviceViewModel.createDevice(
                        type = "laptop",
                        brand = "HP",
                        model = "EliteBook 840",
                        description = "Laptop para uso empresarial",
                        serialNumber = "SN123456",
                        assignedTo = null,
                        status = "available"
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Criar Dispositivo",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Pressione o botão acima para criar um dispositivo de teste.",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
