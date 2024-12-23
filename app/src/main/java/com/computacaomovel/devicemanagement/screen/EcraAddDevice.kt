package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.computacaomovel.devicemanagement.device.DeviceViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EcraAddDevice(
    deviceViewModel: DeviceViewModel,
    onDeviceAdded: () -> Unit,
    onDeviceUpdated: () -> Unit, // Callback para quando um dispositivo é atualizado
    onBack: () -> Unit,
    isEditing: Boolean = false, // Indica se estamos no modo de edição
    initialDeviceData: Map<String, Any>? = null // Dados iniciais para edição, se houver
) {
    LaunchedEffect(Unit) {
        deviceViewModel.clearResultMessage()
    }

    // Dados de entrada (preenchidos com valores iniciais, se em edição)
    val validTypes = listOf("desktop", "laptop", "smartphone", "tablet")
    val validStatuses = listOf("available", "check-out", "broken", "sold")

    var type by remember { mutableStateOf(initialDeviceData?.get("type") as? String ?: validTypes.first()) }
    var brand by remember { mutableStateOf(initialDeviceData?.get("brand") as? String ?: "") }
    var model by remember { mutableStateOf(initialDeviceData?.get("model") as? String ?: "") }
    var description by remember { mutableStateOf(initialDeviceData?.get("description") as? String ?: "") }
    var serialNumber by remember { mutableStateOf(initialDeviceData?.get("serial_number") as? String ?: "") }
    var assignedTo by remember { mutableStateOf(initialDeviceData?.get("assigned_to") as? String ?: "") }
    var status by remember { mutableStateOf(initialDeviceData?.get("status") as? String ?: validStatuses.first()) }

    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }

    val resultMessage = deviceViewModel.result.observeAsState().value ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Dispositivo" else "Adicionar Dispositivo",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mensagem de erro ou sucesso
            if (resultMessage.isNotEmpty()) {
                Text(
                    text = resultMessage,
                    color = if (resultMessage.startsWith("Erro")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Card de entrada dos dados
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Dropdown (não editável em modo edição)
                    OutlinedTextField(
                        value = type,
                        onValueChange = { },
                        label = { Text("Tipo") },
                        readOnly = true,
                        trailingIcon = {
                            DropdownMenu(
                                options = validTypes,
                                selectedOption = type,
                                onOptionSelected = { type = it }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isEditing // Apenas editável em modo de add
                    )

                    // Campos de entrada
                    OutlinedTextField(
                        value = brand,
                        onValueChange = {
                            brand = it
                            brandError = it.isBlank()
                        },
                        label = { Text("Marca") },
                        isError = brandError,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isEditing // Apenas editável em modo de add
                    )

                    OutlinedTextField(
                        value = model,
                        onValueChange = {
                            model = it
                            modelError = it.isBlank()
                        },
                        label = { Text("Modelo") },
                        isError = modelError,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isEditing // Apenas editável em modo de add
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = serialNumber,
                        onValueChange = { serialNumber = it },
                        label = { Text("Número de Série") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = assignedTo,
                        onValueChange = { assignedTo = it },
                        label = { Text("ColaboradorID") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Dropdown Status
                    OutlinedTextField(
                        value = status,
                        onValueChange = { },
                        label = { Text("Status") },
                        readOnly = true,
                        trailingIcon = {
                            DropdownMenu(
                                options = validStatuses,
                                selectedOption = status,
                                onOptionSelected = { status = it }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para guardar
            Button(
                onClick = {
                    brandError = brand.isBlank()
                    modelError = model.isBlank()

                    if (!brandError && !modelError) {
                        if (isEditing) {
                            initialDeviceData?.get("uid")?.let { uid ->
                                deviceViewModel.updateDevice(
                                    uid = uid.toString(),
                                    description = description,
                                    serialNumber = serialNumber,
                                    assignedTo = if (assignedTo.isBlank()) null else assignedTo,
                                    status = status
                                )
                            }
                        } else {
                            deviceViewModel.createDevice(
                                type = type,
                                brand = brand,
                                model = model,
                                description = description,
                                serialNumber = serialNumber,
                                assignedTo = if (assignedTo.isBlank()) null else assignedTo,
                                status = status
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (isEditing) "Atualizar Dispositivo" else "Adicionar Dispositivo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Lógica para redirecionamento baseado no resultado
            LaunchedEffect(resultMessage) {
                if (resultMessage.startsWith("Dispositivo atualizado com sucesso") && isEditing) {
                    onDeviceUpdated()
                } else if (resultMessage.startsWith("Dispositivo criado com sucesso") && !isEditing) {
                    onDeviceAdded()
                }
            }
        }
    }
}

@Composable
fun DropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

