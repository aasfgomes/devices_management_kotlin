package com.computacaomovel.devicemanagement.user

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

// Classe que representa o ViewModel responsável pela lógica de autenticação
class UserViewModel : ViewModel() {

    // Instância da base de dados Firestore
    private val db: FirebaseFirestore = Firebase.firestore

    // Estado para armazenar o resultado da autenticação

    private val _result = mutableStateOf("")
    val result: State<String> = _result

    // Função para autenticar o utilizador com base no username e password
    fun authenticate(username: String, password: String) {

        // Usa o viewModelScope para iniciar uma coroutine, o que permite executar a operação de auth de forma assíncrona sem bloquear a interface do user
        viewModelScope.launch {
            // Consulta à collection "user" no Firebase para verificar o username e a password
            db.collection("user")
                .whereEqualTo("username", username) // Filtra pelo username
                .whereEqualTo("password", password) // Filtra pela password
                .get()
                .addOnSuccessListener { documents ->
                    // Se encontrar um doc com os dados passados, significa que a autenticação passa
                    if (documents.size() > 0) {
                        _result.value = "Authentication successful!"
                    } else {
                        _result.value = "Authentication failed." // Caso contrário, erro
                    }
                }
                .addOnFailureListener {
                    // Caso ocorra um erro na query, atualiza o resultado com a mensagem de erro
                    _result.value = "Error: ${it.message}"
                }
        }
    }
}
