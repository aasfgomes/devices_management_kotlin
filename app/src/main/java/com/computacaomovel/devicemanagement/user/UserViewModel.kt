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

    /**
     * Atualiza a mensagem de resultado para mostrar no ecrã
     * @param message Mensagem a ser mostrada (ex: sucesso ou erro).
     */

    fun updateResultMessage(message: String) {
        _result.value = message
    }

    /**
     * Autentica um user com base no username e password.
     * @param username Nome de user.
     * @param password Palavra-passe do user.
     */

    fun authenticate(username: String, password: String) {
        viewModelScope.launch { // Lança a tarefa com uma coroutine
            try {
                val hashedPassword = hashPassword(password) // hash para a password
                // Procura o user no firebase com os dados inseridos
                val documents = db.collection("user")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", hashedPassword)
                    .get()
                    .await()
                if (documents.size() > 0) {
                    // Autenticação bem-sucedida
                    _result.value = "Authentication successful!"
                    _isAuthenticated.value = true
                } else {
                    // Autenticação falhou
                    _result.value = "Authentication failed."
                    _isAuthenticated.value = false
                }
            } catch (e: Exception) {
                // Erro ao autenticar
                _result.value = "Error: ${e.message}"
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Autentica o user com uma conta Google.
     * @param idToken Token de autenticação Google.
     */

    fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                // Cria as credenciais com base no token do Google
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await() // Autentica com Firebase Auth

                _result.value = "Authentication with Google successful!"
                _isAuthenticated.value = true

                // Obtém informações do user autenticado
                val user = auth.currentUser
                user?.let {
                    // Armazena as informações do user na base de dados firebae
                    val userData = hashMapOf(
                        "uid" to it.uid,
                        "username" to it.displayName,
                        "email" to it.email
                    )
                    db.collection("user").document(it.uid).set(userData).await()
                }
            } catch (e: Exception) {
                // Falha na autenticação
                _result.value = "Google Authentication failed: ${e.message}"
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
    fun registerNewUser(username: String, password: String, email: String) {
        val hashedPassword = hashPassword(password) // Hasheia a palavra-passe para segurança
        auth.createUserWithEmailAndPassword(email, password) // Cria a conta no Firebase Auth
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Se a conta foi criada, armazena o utilizador na Firestore
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "password" to hashedPassword
                    )
                    db.collection("user").document(auth.currentUser?.uid ?: "").set(user)
                        .addOnSuccessListener {
                            _result.value = "Registration successful!" // Registo bem-sucedido
                        }
                        .addOnFailureListener {
                            _result.value = "Registration failed: ${it.message}" // Falha ao salvar no Firestore
                        }
                } else {
                    _result.value = "Registration failed: ${task.exception?.message}" // Falha na criação do utilizador
                }
            }
    }

    /**
     * Hasheia a password usando SHA-256 para maior segurança.
     * @param password Palavra-passe a ser hasheada.
     * @return Palavra-passe hasheada em formato hexadecimal.
     */
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256") // Instância do algoritmo SHA-256
        val hash = md.digest(password.toByteArray()) // Hasheia os bytes da palavra-passe
        return hash.joinToString("") { "%02x".format(it) } // Retorna em formato hexadecimal
    }
}
