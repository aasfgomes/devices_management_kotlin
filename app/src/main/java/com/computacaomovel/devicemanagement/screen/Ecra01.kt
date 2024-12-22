package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.computacaomovel.devicemanagement.device.DeviceViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Ecra01(
    deviceViewModel: DeviceViewModel,
    navController: NavController,
    onAddDeviceClick: () -> Unit
) {
    // Observa os estados do ViewModel
    val allDevices = deviceViewModel.deviceList.value ?: emptyList()
    var filteredDevices by remember { mutableStateOf(allDevices) }
    var searchQuery by remember { mutableStateOf("") }
    val resultMessage = deviceViewModel.result.value ?: ""

    // Efeito para buscar os dispositivos ao carregar o ecrã
    LaunchedEffect(Unit) {
        deviceViewModel.getDevice()
    }

    // Atualiza os dispositivos filtrados quando a lista de dispositivos ou a pesquisa muda
    LaunchedEffect(allDevices, searchQuery) {
        filteredDevices = if (searchQuery.isBlank()) {
            allDevices
        } else {
            allDevices.filter {
                (it["uid"]?.toString()?.contains(searchQuery, ignoreCase = true) == true) ||
                        (it["type"]?.toString()?.contains(searchQuery, ignoreCase = true) == true) ||
                        (it["model"]?.toString()?.contains(searchQuery, ignoreCase = true) == true) ||
                        (it["status"]?.toString()?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Pesquisar dispositivos") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableHeaderCell("UID", Modifier.weight(1f))
                TableHeaderCell("Tipo", Modifier.weight(1f))
                TableHeaderCell("Modelo", Modifier.weight(1f))
                TableHeaderCell("Status", Modifier.weight(1f))
            }

            Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)

            // Conteúdo da tabela
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredDevices) { device ->
                    if (device is Map<*, *> && device.keys.all { it is String }) {
                        DeviceTableRow(
                            device = device as Map<String, Any>,
                            navController = navController
                        )
                    }
                }
            }
        }

        // Botão de adicionar
        FloatingActionButton(
            onClick = onAddDeviceClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar dispositivo",
                tint = Color.White
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
        modifier = modifier.padding(8.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
fun DeviceTableRow(device: Map<String, Any>, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val deviceId = device["uid"].toString()
                navController.navigate("deviceInformation/$deviceId")
            }
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(device["uid"]?.toString() ?: "-", Modifier.weight(1f))
        TableCell(device["type"]?.toString() ?: "-", Modifier.weight(1f))
        TableCell(device["model"]?.toString() ?: "-", Modifier.weight(1f))
        TableCell(device["status"]?.toString() ?: "-", Modifier.weight(1f))
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
