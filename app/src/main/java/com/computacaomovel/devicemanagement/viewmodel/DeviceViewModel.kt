package com.computacaomovel.devicemanagement.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class DeviceViewModel : ViewModel() {

    // Referências ao Firebase Firestore e Firebase Auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Lista de dispositivos
    private val _deviceList = MutableLiveData<List<Map<String, Any>>>()
    val deviceList: LiveData<List<Map<String, Any>>> get() = _deviceList

    // Definições dos tipos e status válidos
    private val validTypes = listOf("desktop", "laptop", "smartphone", "tablet")
    private val validStatuses = listOf("available", "check-out", "broken", "sold")

    // Estado de resposta para erros ou sucessos
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    /**
     * Função auxiliar para obter o UID do utilizador atual
     */
    private fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Função auxiliar para obter o próximo UID incremental
     */
    private suspend fun getNextUid(): Int {
        val snapshot = db.collection("devices").get().await()
        val currentCount = snapshot.size()
        return currentCount + 1 // UID incremental
    }

    /**
     * Função para atualizar o log global no Firestore
     */
    private fun updateGlobalLog(
        operation: String,
        deviceUid: String,
        performedBy: String,
        details: Map<String, Any>
    ) {
        viewModelScope.launch {
            try {
                val logEntry = hashMapOf(
                    "operation" to operation,
                    "device_uid" to deviceUid,
                    "performed_by" to performedBy,
                    "timestamp" to System.currentTimeMillis(),
                    "details" to details
                )

                val logsRef = db.collection("logs").document("global_logs")

                // Atualiza o array 'operations' com o novo log
                logsRef.update("operations", com.google.firebase.firestore.FieldValue.arrayUnion(logEntry))
                    .addOnSuccessListener {
                        println("Log adicionado com sucesso ao documento global.")
                    }
                    .addOnFailureListener { e ->
                        println("Erro ao atualizar log: ${e.message}")
                    }
            } catch (e: Exception) {
                println("Erro ao adicionar log: ${e.message}")
            }
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
                    _result.postValue("Erro: 'Brand' e 'Model' são obrigatórios.")
                    return@launch
                }

                // Valida o assignedTo, se fornecido
                if (assignedTo != null) {
                    val userSnapshot = db.collection("user").document(assignedTo).get().await()
                    if (!userSnapshot.exists()) {
                        _result.postValue("Erro: O user com UID '$assignedTo' não existe.")
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

                db.collection("devices").document(nextUid.toString()).set(device).await()

                // Corrigido: Filtrar valores nulos e garantir compatibilidade com Any
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


    /**
     * Função para atualizar um dispositivo existente.
     */
    fun updateDevice(
        uid: String,
        description: String? = null,
        serialNumber: String? = null,
        assignedTo: String? = null,
        status: String? = null
    ) {
        viewModelScope.launch {
            try {
                val currentUserUid = getCurrentUserUid()
                if (currentUserUid == null) {
                    _result.postValue("Erro: User não autenticado.")
                    return@launch
                }

                val deviceRef = db.collection("devices").document(uid)
                val snapshot = deviceRef.get().await()

                if (snapshot.exists()) {
                    // Valida o assignedTo
                    if (assignedTo != null) {
                        val userSnapshot = db.collection("user").document(assignedTo).get().await()
                        if (!userSnapshot.exists()) {
                            _result.postValue("Erro: O utilizador com UID '$assignedTo' não existe.")
                            return@launch
                        }
                    }

                    val updates = mutableMapOf<String, Any>()
                    description?.let { updates["description"] = it }
                    serialNumber?.let { updates["serial_number"] = it }
                    assignedTo?.let { updates["assigned_to"] = it }
                    status?.let {
                        if (it in validStatuses) {
                            updates["status"] = it
                        } else {
                            throw IllegalArgumentException("Status inválido.")
                        }
                    }

                    deviceRef.update(updates).await()
                    updateGlobalLog("update", uid, currentUserUid, updates.filterValues { it != null })
                    _result.postValue("Dispositivo atualizado com sucesso!")
                } else {
                    _result.postValue("Erro: Dispositivo não encontrado.")
                }
            } catch (e: Exception) {
                _result.postValue("Erro ao atualizar dispositivo: ${e.message}")
            }
        }
    }

    /**
     * Função para eliminar um dispositivo.
     */
    fun deleteDevice(uid: String) {
        viewModelScope.launch {
            try {
                val currentUserUid = getCurrentUserUid()
                if (currentUserUid == null) {
                    _result.postValue("Erro: User não autenticado.")
                    return@launch
                }

                val deviceRef = db.collection("devices").document(uid)
                val snapshot = deviceRef.get().await()

                if (snapshot.exists()) {
                    val deviceData = snapshot.data ?: emptyMap()
                    val filteredDeviceData = deviceData.mapValues { it.value ?: "" }
                    deviceRef.delete().await()
                    updateGlobalLog("delete", uid, currentUserUid, filteredDeviceData)
                    _result.postValue("Dispositivo eliminado com sucesso.")
                } else {
                    _result.postValue("Erro: Dispositivo não encontrado.")
                }
            } catch (e: Exception) {
                _result.postValue("Erro ao eliminar dispositivo: ${e.message}")
            }
        }
    }

    fun getDevice() {
        viewModelScope.launch {
            try {
                // Consulta todos os documentos na coleção "devices"
                val snapshot = db.collection("device").get().await()

                if (!snapshot.isEmpty) {
                    // Transforma os resultados para uma lista de mapas
                    val devices = snapshot.documents.map { it.data ?: emptyMap() }
                    println("DEBUG: Dispositivos encontrados: $devices") // Log para depuração
                    _deviceList.value = devices // Atualiza o LiveData com os dispositivos encontrados
                    //_result.value = "Dispositivos encontrados: ${devices.size}"
                } else {
                    println("nada")
                    _deviceList.value = emptyList()
                    _result.value = "Nenhum dispositivo encontrado."
                }
            } catch (e: Exception) {
                println("nada dispositivos")
                _result.value = "Erro ao procurar os dispositivos: ${e.message}"
            }
        }
    }
}


