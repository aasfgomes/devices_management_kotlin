package com.computacaomovel.devicemanagement.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DeviceViewModel : ViewModel() {

    // Referências ao Firebase Firestore e Firebase Auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Lista de dispositivos
    private val _deviceList = MutableLiveData<List<Map<String, Any>>>()
    val deviceList: LiveData<List<Map<String, Any>>> get() = _deviceList

    // Estado de resposta para erros ou sucessos
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    // Definições dos tipos e status válidos
    private val validTypes = listOf("desktop", "laptop", "smartphone", "tablet")
    private val validStatuses = listOf("available", "check-out", "broken", "sold")

    /**
     * Função auxiliar para obter o UID do user atual * usamos nos logs *
     */
    private fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Função auxiliar para obter o próximo UID incremental
     */
    private suspend fun getNextUid(): Int {
        val snapshot = db.collection("device").get().await()
        val currentCount = snapshot.size()
        return currentCount + 1
    }

    /**
     * Função para atualizar o log global no Firestore
     */
    private suspend fun updateGlobalLog(
        operation: String,
        deviceUid: String,
        performedBy: String,
        details: Map<String, Any>
    ) {
        val logEntry = hashMapOf(
            "operation" to operation,
            "device_uid" to deviceUid,
            "created_by" to performedBy, // ID de quem cria o device ou faz alguma coisa
            "timestamp" to System.currentTimeMillis(),
            "details" to details
        )
        try {
            val logsRef = db.collection("logs").document("global_logs")

            // Verifica se o documento existe; cria caso não exista
            val snapshot = logsRef.get().await()
            if (!snapshot.exists()) {
                logsRef.set(hashMapOf("operations" to listOf<Map<String, Any>>())).await()
            }

            // Adiciona o novo log
            logsRef.update(
                "operations",
                com.google.firebase.firestore.FieldValue.arrayUnion(logEntry)
            ).await()
        } catch (e: Exception) {
            throw Exception("Erro ao atualizar o log global: ${e.message}")
        }
    }



    /**
     * Função para criar um novo dispositivo.
     */
    fun createDevice(
        type: String,
        brand: String,
        model: String,
        description: String? = null,
        serialNumber: String? = null,
        assignedTo: String? = null,
        status: String = "available"
    ) {
        viewModelScope.launch {
            try {
                val currentUserUid = getCurrentUserUid()
                if (currentUserUid == null) {
                    _result.postValue("Erro: User não autenticado.")
                    return@launch
                }

                if (type !in validTypes) {
                    _result.postValue("Erro: O tipo de dispositivo '$type' não é válido.")
                    return@launch
                }

                if (status !in validStatuses) {
                    _result.postValue("Erro: O status '$status' não é válido.")
                    return@launch
                }

                if (brand.isBlank() || model.isBlank()) {
                    _result.postValue("Erro: 'Marca' e 'Modelo' são obrigatórios.")
                    return@launch
                }

                if (!assignedTo.isNullOrBlank()) {
                    val userSnapshot = db.collection("user").document(assignedTo).get().await()
                    if (!userSnapshot.exists()) {
                        _result.postValue("Erro: O colaborador com UID '$assignedTo' não existe.")
                        return@launch
                    }
                }

                val nextUid = getNextUid()

                val device = hashMapOf(
                    "uid" to nextUid,
                    "type" to type,
                    "brand" to brand,
                    "model" to model,
                    "description" to description,
                    "serial_number" to serialNumber,
                    "assigned_to" to assignedTo,
                    "status" to status
                )

                db.collection("device").document(nextUid.toString()).set(device).await()

                updateGlobalLog(
                    operation = "create",
                    deviceUid = nextUid.toString(),
                    performedBy = currentUserUid,
                    details = device.filterValues { it != null }.mapValues { it.value as Any }
                )

                _result.postValue("Dispositivo criado com sucesso! UID: $nextUid")
            } catch (e: Exception) {
                _result.postValue("Erro ao criar dispositivo: ${e.message}")
            }
        }
    }

    fun getDevice() {
        viewModelScope.launch {
            try {
                // Consulta todos os documentos na coleção "device"
                val snapshot = db.collection("device").get().await()

                if (!snapshot.isEmpty) {
                    // Transforma os resultados para uma lista de mapas e ordena pelo UID
                    val devices = snapshot.documents
                        .map { it.data ?: emptyMap() }
                        .sortedBy { it["uid"] as? Int }

                    _deviceList.value = devices
                } else {
                    _deviceList.value = emptyList()
                    _result.value = "Nenhum dispositivo encontrado."
                }
            } catch (e: Exception) {
                _result.value = "Erro ao buscar dispositivos: ${e.message}"
            }
        }
    }

    fun clearResultMessage() {
        _result.value = ""
    }
}
