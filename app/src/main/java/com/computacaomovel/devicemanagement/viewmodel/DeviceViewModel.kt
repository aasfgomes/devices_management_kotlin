package com.computacaomovel.devicemanagement.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.lang.Exception

class DeviceViewModel : ViewModel() {

    // Ref ao Firebase Firestore
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Definições dos tipos e status válidos
    private val validTypes = listOf("desktop", "laptop", "smartphone", "tablet")
    private val validStatuses = listOf("available", "check-out", "broken", "sold")

    // Estado de resposta para erros ou sucessos
    private val _result = mutableListOf<String>()
    val result: List<String> get() = _result


    // Fun auxilar para conseguir ir buscar o proximo uid
    private suspend fun getNextUid(): Int {
        val snapshot = db.collection("devices").get().await()
        val currentCount = snapshot.size()
        return currentCount + 1 // UID incremental
    }

    /**
     * Função para criar um novo device.
     *
     * @param type Tipo do dispositivo (desktop, laptop, smartphone, tablet).
     * @param brand Marca  (obrigatório).
     * @param model Modelo  (obrigatório).
     * @param description Descrição (opcional).
     * @param serialNumber SN (opcional).
     * @param assignedTo Atribuído a um user (default null caso não passe nada).
     * @param status Status  (default "available").
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
                // Valida o tipo do dispositivo
                if (type !in validTypes) {
                    _result.add("Erro: O tipo de dispositivo '$type' não é válido.")
                    return@launch
                }

                // Valida o status do dispositivo
                if (status !in validStatuses) {
                    _result.add("Erro: O status '$status' não é válido.")
                    return@launch
                }

                // Valida os campos obrigatórios
                if (brand.isBlank() || model.isBlank()) {
                    _result.add("Erro: 'Brand' e 'Model' são obrigatórios.")
                    return@launch
                }

                // Obtem o próximo ID incremental
                val nextUid = getNextUid()

                // Dados do dispositivo
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

                // Guarda no firestore
                db.collection("devices").document(nextUid.toString()).set(device).await()
                _result.add("Dispositivo criado com sucesso! UID: $nextUid")
            } catch (e: Exception) {
                _result.add("Erro ao criar dispositivo: ${e.message}")
            }
        }
    }

    /**
     * Fun auxiliar para obter o próximo UID incremental.
     */

}
