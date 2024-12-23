package com.computacaomovel.devicemanagement.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.device.DeviceViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Ecra02(deviceViewModel: DeviceViewModel) {
    val logs by deviceViewModel.logs.observeAsState(emptyList())
    val searchText = remember { mutableStateOf("") }
    val filteredLogs = logs.filter {
        it["operation"]?.toString()?.contains(searchText.value, ignoreCase = true) ?: true
    }

    // Vai buscar logo os logs quando a pagina é carregada
    LaunchedEffect(Unit) {
        deviceViewModel.fetchLogs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // SearchBar
            OutlinedTextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                label = { Text("Procurar por operação") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (filteredLogs.isEmpty()) {
                // Exibe mensagem se não houver logs
                Text(
                    text = "Nenhum log encontrado",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Scroll
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredLogs) { log ->
                        ExpandableLogCard(log, deviceViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableLogCard(log: Map<String, Any>, deviceViewModel: DeviceViewModel) {
    var isExpanded by remember { mutableStateOf(false) }
    val collaboratorName = remember { mutableStateOf("-") }

    // Buscar o nome do colaborador com base no UID
    LaunchedEffect(log["performed_by"]) {
        val performedBy = log["performed_by"]?.toString() ?: ""
        if (performedBy.isNotBlank()) {
            deviceViewModel.getCollaboratorName(performedBy) { name ->
                collaboratorName.value = name ?: "Desconhecido"
            }
        } else {
            collaboratorName.value = "Não especificado"
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded } // Alterna expansão ao clicar
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título do cartão (sempre visível)
            Text(
                text = "Operação: ${log["operation"] ?: "Desconhecida"}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            // Conteúdo adicional (visível apenas quando expandido)
            if (isExpanded) {
                Text(
                    text = "Dispositivo UID: ${log["device_uid"] ?: "Desconhecido"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Feito por: ${collaboratorName.value}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Detalhes: ${log["details"] ?: "Sem detalhes"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Timestamp: ${
                        (log["timestamp"] as? Long)?.let {
                            java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(it)
                        } ?: "Desconhecido"
                    }",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}





