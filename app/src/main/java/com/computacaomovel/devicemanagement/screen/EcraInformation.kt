package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.device.DeviceViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EcraInformation(
    deviceId: String?,
    deviceViewModel: DeviceViewModel,
    onBack: () -> Unit,
    onDeleteDevice: () -> Unit,
    onUpdateDevice: () -> Unit // Callback para atualização
) {
    // Atualiza a lista de dispositivos sempre que o `deviceId` muda
    LaunchedEffect(deviceId) {
        deviceViewModel.getDevice()
        deviceViewModel.fetchUserType() // Garante que o tipo do user seja carregado * preciso disto para conseguir manipular os botões *
    }

    // Busca o dispositivo com base no ID
    val device = deviceViewModel.deviceList.observeAsState().value?.find { it["uid"].toString() == deviceId }

    // Obtém o tipo de usuário do ViewModel
    val userType = deviceViewModel.userType.observeAsState(initial = "user").value

    var collaboratorName by remember { mutableStateOf<String?>(null) }

    // Vai buscar o nome do colaborador com base no assigned_to * em vez de ver o uid dele, vamos buscar o nome *
    LaunchedEffect(device) {
        val assignedTo = device?.get("assigned_to")?.toString()
        if (!assignedTo.isNullOrEmpty()) {
            deviceViewModel.getCollaboratorName(assignedTo) { name ->
                collaboratorName = name
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Dispositivo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Mostra os botões apenas se o tipo de utilizador não for user
                    if (userType != "user") {
                        IconButton(onClick = onUpdateDevice) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Atualizar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDeleteDevice) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (device != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Título
                    Text(
                        text = "Informações do Dispositivo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Mostra os campos do dispositivo
                    InformationRowDevice("UID", device["uid"]?.toString() ?: "-")
                    InformationRowDevice("Tipo", device["type"]?.toString() ?: "-")
                    InformationRowDevice("Marca", device["brand"]?.toString() ?: "-")
                    InformationRowDevice("Modelo", device["model"]?.toString() ?: "-")
                    InformationRowDevice("Descrição", device["description"]?.toString() ?: "Sem descrição")
                    InformationRowDevice("Número de Série", device["serial_number"]?.toString() ?: "-")
                    InformationRowDevice("Status", device["status"]?.toString() ?: "-")
                    Spacer(modifier = Modifier.height(16.dp))
                    InformationRowDevice("Colaborador", collaboratorName ?: "-")
                }
            }
        }
    }
}

@Composable
fun InformationRowDevice(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
