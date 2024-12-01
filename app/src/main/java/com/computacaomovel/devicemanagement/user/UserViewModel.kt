package com.computacaomovel.devicemanagement.user

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

/**
 * ViewModel para gerir a lógica dos users (auth e registo).
 */
class UserViewModel : ViewModel() {

    // Referências para Firebase Firestore e Firebase Auth
    private val db: FirebaseFirestore = Firebase.firestore // Base de dados Firestore
    private val auth: FirebaseAuth = Firebase.auth // Serviço de autenticação do Firebase

    // Estado para armazenar mensagens de resultado
    private val _result = mutableStateOf("") // Mensagem atual (ex: sucesso ou erro)
    val result: State<String> = _result

    // Estado para indicar se o user está autenticado
    private val _isAuthenticated = MutableLiveData<Boolean>() // Indica o estado da autenticação
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    // Estado para indicar se o user está registado
    private val _isRegistered = MutableLiveData<Boolean>() // Estado de registro
    val isRegistered: LiveData<Boolean> = _isRegistered

    /**
     * Atualiza a mensagem de resultado para mostrar no ecrã.
     * @param message Mensagem a ser mostrada.
     */
    fun updateResultMessage(message: String) {
        _result.value = message
    }

    /**
     * Autentica um user com base no username e password.
     * @param username Nome de user.
     * @param password Palavra-passe do user.
     */
    fun authenticate(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val hashedPassword = hashPassword(password) // Hasheia a palavra-passe
                val documents = db.collection("user")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", hashedPassword)
                    .get()
                    .await()
                if (documents.size() > 0) {
                    _result.value = "Authentication successful!" // Sucesso
                    _isAuthenticated.value = true
                    onSuccess() // Executa a ação de sucesso
                } else {
                    _result.value = "Authentication failed." // Falha
                    _isAuthenticated.value = false
                }
            } catch (e: Exception) {
                _result.value = "Error: ${e.message}" // Erro
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Autentica o user com uma conta Google.
     * @param idToken Token de autenticação Google.
     */
    fun authenticateWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await() // Autentica com Firebase Auth

                _result.value = "Authentication with Google successful!" // Sucesso
                _isAuthenticated.value = true
                onSuccess() // Executa a ação de sucesso
            } catch (e: Exception) {
                _result.value = "Google Authentication failed: ${e.message}" // Falha
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Regista um novo utilizador na aplicação.
     * @param username Nome de utilizador.
     * @param password Palavra-passe.
     * @param email Endereço de email do utilizador.
     */
    fun registerNewUser(username: String, password: String, email: String, onSuccess: () -> Unit) {
        val hashedPassword = hashPassword(password) // Hasheia a palavra-passe
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "password" to hashedPassword
                )
                db.collection("user").document(auth.currentUser?.uid ?: "").set(user)
                    .addOnSuccessListener {
                        _result.value = "Registration successful!" // Sucesso
                        _isRegistered.value = true
                        onSuccess() // Executa a ação de sucesso
                    }
                    .addOnFailureListener {
                        _result.value = "Registration failed: ${it.message}" // Falha no Firestore
                        _isRegistered.value = false
                    }
            } else {
                _result.value = "Registration failed: ${task.exception?.message}" // Falha no Auth
                _isRegistered.value = false
            }
        }
    }

    /**
     * Hasheia a password usando SHA-256 para maior segurança.
     * @param password Palavra-passe.
     * @return Palavra-passe hasheada.
     */
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256") // Instância do SHA-256
        val hash = md.digest(password.toByteArray()) // Hasheia os bytes da palavra-passe
        return hash.joinToString("") { "%02x".format(it) } // Retorna em formato hexadecimal
    }
}
