package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.device.DeviceViewModel

@Composable
fun Ecra01(deviceViewModel: DeviceViewModel, onAddDeviceClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    // Observa os estados do ViewModel
    val deviceList = deviceViewModel.deviceList.value ?: emptyList()
    val resultMessage = deviceViewModel.result.value ?: ""

    // Efeito para buscar os dispositivos ao carregar o ecrã
    LaunchedEffect(Unit) {
        deviceViewModel.getDevice()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            // Mensagem de resultado
            if (resultMessage.isNotEmpty()) {
                Text(
                    text = resultMessage,
                    color = if (resultMessage.startsWith("Erro")) Color.Red else Color.Green,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cabeçalho da tabela
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableHeaderCell("UID", Modifier.weight(1f))
                TableHeaderCell("Tipo", Modifier.weight(1f))
                TableHeaderCell("Modelo", Modifier.weight(1f))
                TableHeaderCell("Status", Modifier.weight(1f))
            }

            Divider(color = Color.Gray, thickness = 1.dp)

            // Conteúdo da tabela
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deviceList) { device ->
                    if (device is Map<*, *> && device.keys.all { it is String }) {
                        DeviceTableRow(device = device as Map<String, Any>, onDeviceClick = {})
                    }
                }
            }
        }

        // Botão de add
        FloatingActionButton(
            onClick = onAddDeviceClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 16.dp), // Posicao
            containerColor = Color.White //
        ) {
            Text(
                text = "+",
                color = Color.Black, // Cor do ícon
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun DeviceTableRow(device: Map<String, Any>, onDeviceClick: (Map<String, Any>) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onDeviceClick(device) }, // Torna a linha clicável
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(device["uid"]?.toString() ?: "N/A", Modifier.weight(1f))
        TableCell(device["type"]?.toString() ?: "N/A", Modifier.weight(1f))
        TableCell(device["model"]?.toString() ?: "N/A", Modifier.weight(1f))
        TableCell(device["status"]?.toString() ?: "N/A", Modifier.weight(1f))
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface
    )
}



