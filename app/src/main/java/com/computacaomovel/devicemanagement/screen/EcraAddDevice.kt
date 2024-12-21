package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.R
import com.computacaomovel.devicemanagement.device.DeviceViewModel

@Composable
fun EcraAddDevice(
    deviceViewModel: DeviceViewModel,
    onDeviceAdded: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        deviceViewModel.clearResultMessage()
    }

    val validTypes = listOf("desktop", "laptop", "smartphone", "tablet")
    val validStatuses = listOf("available", "check-out", "broken", "sold")

    var type by remember { mutableStateOf(validTypes.first()) }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(validStatuses.first()) }

    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }

    val resultMessage = deviceViewModel.result.observeAsState().value ?: ""

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Adicionar Novo Dispositivo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exibir mensagem de erro ou sucesso
            if (resultMessage.isNotEmpty()) {
                Text(
                    text = resultMessage,
                    color = if (resultMessage.startsWith("Erro")) Color.Red else Color.Green,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown para Tipo
            StableDropdownMenu(
                label = "Tipo",
                options = validTypes,
                selectedOption = type,
                onOptionSelected = { type = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para Marca
            OutlinedTextField(
                value = brand,
                onValueChange = {
                    brand = it
                    brandError = it.isBlank()
                },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth(),
                isError = brandError
            )
            if (brandError) {
                Text(
                    text = "Marca é obrigatória",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para Modelo
            OutlinedTextField(
                value = model,
                onValueChange = {
                    model = it
                    modelError = it.isBlank()
                },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth(),
                isError = modelError
            )
            if (modelError) {
                Text(
                    text = "Modelo é obrigatório",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para Descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para Número de Série
            OutlinedTextField(
                value = serialNumber,
                onValueChange = { serialNumber = it },
                label = { Text("Número de Série") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para Colaborador (assignedTo)
            OutlinedTextField(
                value = assignedTo,
                onValueChange = { assignedTo = it },
                label = { Text("Colaborador") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown para Status
            StableDropdownMenu(
                label = "Status",
                options = validStatuses,
                selectedOption = status,
                onOptionSelected = { status = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para adicionar dispositivo
            Button(
                onClick = {
                    brandError = brand.isBlank()
                    modelError = model.isBlank()

                    if (!brandError && !modelError) {
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Adicionar Dispositivo",
                    color = Color.White
                )
            }
            // Redireciona para a página principal após sucesso
            LaunchedEffect(resultMessage) {
                if (resultMessage.startsWith("Dispositivo criado com sucesso")) {
                    onDeviceAdded() // Navega para a página principal
                }
            }
        }


            // Botão flutuante para voltar
        FloatingActionButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Voltar"
            )
        }
    }
}

@Composable
fun StableDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { }, // Read-only
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.dropdown),
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )

        DropdownMenu(
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
