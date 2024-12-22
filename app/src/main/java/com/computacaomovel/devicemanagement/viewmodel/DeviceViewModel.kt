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

    // Função auxiliar para obter o UID do usuário atual
    private fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }


    // Atualiza os logs globais no Firestore
    private suspend fun updateGlobalLog(
        operation: String,
        deviceUid: String,
        performedBy: String,
        details: Map<String, Any>
    ) {
        val logEntry = hashMapOf(
            "operation" to operation,
            "device_uid" to deviceUid,
            "created_by" to performedBy,
            "timestamp" to System.currentTimeMillis(),
            "details" to details
        )
        try {
            val logsRef = db.collection("logs").document("global_logs")
            val snapshot = logsRef.get().await()
            if (!snapshot.exists()) {
                logsRef.set(hashMapOf("operations" to listOf<Map<String, Any>>())).await()
            }
            logsRef.update(
                "operations",
                com.google.firebase.firestore.FieldValue.arrayUnion(logEntry)
            ).await()
        } catch (e: Exception) {
            throw Exception("Erro ao atualizar o log global: ${e.message}")
        }
    }

    // Cria um novo dispositivo
    // Atualize o método `getNextUid` para evitar colisões
    private suspend fun getNextUid(): String {
        val snapshot = db.collection("device").get().await()
        // Gera um UID único baseado em um timestamp ou UUID
        return System.currentTimeMillis().toString() // Alternativa: UUID.randomUUID().toString()
    }
    // Método para obter o próximo UID incremental
    private suspend fun getNextUidDevice(): Int {
        return try {
            val snapshot = db.collection("device").get().await()
            // Busca o maior UID existente e incrementa
            val maxUid = snapshot.documents
                .mapNotNull { it.getLong("uid") } // Obtém os UIDs existentes
                .maxOrNull() ?: 0 // Se não houver UID, começa com 0
            (maxUid + 1).toInt()
        } catch (e: Exception) {
            throw Exception("Erro ao obter próximo UID: ${e.message}")
        }
    }

    // Função para criar um dispositivo com UID incremental
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
                    _result.postValue("Erro: Usuário não autenticado.")
                    return@launch
                }

                if (type !in validTypes) {
                    _result.postValue("Erro: Tipo de dispositivo '$type' inválido.")
                    return@launch
                }

                if (status !in validStatuses) {
                    _result.postValue("Erro: Status '$status' inválido.")
                    return@launch
                }

                if (brand.isBlank() || model.isBlank()) {
                    _result.postValue("Erro: Marca e Modelo são obrigatórios.")
                    return@launch
                }

                if (!assignedTo.isNullOrBlank()) {
                    val userSnapshot = db.collection("user").document(assignedTo).get().await()
                    if (!userSnapshot.exists()) {
                        _result.postValue("Erro: Colaborador com UID '$assignedTo' não existe.")
                        return@launch
                    }
                }

                // Obtém o próximo UID incremental
                val nextUid = getNextUidDevice()
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
                getDevice() // Atualiza a lista de dispositivos após criar
            } catch (e: Exception) {
                _result.postValue("Erro ao criar dispositivo: ${e.message}")
            }
        }
    }



    // Busca a lista de dispositivos
    fun getDevice() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("device").get().await()
                if (!snapshot.isEmpty) {
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

    // Limpa a mensagem de resultado
    fun clearResultMessage() {
        _result.value = ""
    }

    // Busca o nome de um colaborador pelo UID
    fun getCollaboratorName(uid: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val userSnapshot = db.collection("user").document(uid).get().await()
                if (userSnapshot.exists()) {
                    val name = userSnapshot.getString("username")
                    callback(name)
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    // Remove um dispositivo pelo UID
    fun deleteDevice(deviceId: String?, onComplete: (Boolean) -> Unit) {
        if (deviceId.isNullOrEmpty()) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            try {
                db.collection("device").document(deviceId).delete().await()
                _deviceList.value = _deviceList.value?.filterNot { it["uid"].toString() == deviceId }
                _result.postValue("Dispositivo removido com sucesso!")
                onComplete(true)
            } catch (e: Exception) {
                _result.postValue("Erro ao remover dispositivo: ${e.message}")
                onComplete(false)
            }
        }
    }

    fun updateDevice(
        uid: String,
        description: String?,
        serialNumber: String?,
        assignedTo: String?,
        status: String
    ) {
        viewModelScope.launch {
            try {
                val currentUserUid = getCurrentUserUid()
                if (currentUserUid == null) {
                    _result.postValue("Erro: Usuário não autenticado.")
                    return@launch
                }

                // Validação dos campos que podem ser atualizados
                if (status !in validStatuses) {
                    _result.postValue("Erro: Status '$status' inválido.")
                    return@launch
                }

                if (!assignedTo.isNullOrBlank()) {
                    val userSnapshot = db.collection("user").document(assignedTo).get().await()
                    if (!userSnapshot.exists()) {
                        _result.postValue("Erro: Colaborador com UID '$assignedTo' não existe.")
                        return@launch
                    }
                }

                // Dados permitidos para atualização (remove valores nulos)
                val updatedFields = mutableMapOf<String, Any>(
                    "description" to (description ?: ""),
                    "serial_number" to (serialNumber ?: ""),
                    "assigned_to" to (assignedTo ?: ""),
                    "status" to status
                ).filter { (_, value) -> value.toString().isNotBlank() } // Apenas valores válidos

                if (updatedFields.isEmpty()) {
                    _result.postValue("Erro: Nenhuma alteração detectada.")
                    return@launch
                }

                // Atualização no Firestore
                db.collection("device").document(uid).update(updatedFields).await()

                // Atualizar logs globais
                updateGlobalLog(
                    operation = "update",
                    deviceUid = uid,
                    performedBy = currentUserUid,
                    details = updatedFields
                )

                _result.postValue("Dispositivo atualizado com sucesso!")
                getDevice() // Atualiza a lista de dispositivos
            } catch (e: Exception) {
                _result.postValue("Erro ao atualizar dispositivo: ${e.message}")
            }
        }
    }


}
