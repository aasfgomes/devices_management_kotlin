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
import com.google.firebase.firestore.SetOptions
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
    private val _isRegistered = MutableLiveData<Boolean>() // Estado de registo
    val isRegistered: LiveData<Boolean> = _isRegistered

    /**
     * Atualiza a mensagem de resultado para mostrar no ecrã.
     * @param message Mensagem a ser mostrada.
     */
    fun updateResultMessage(message: String) {
        _result.value = message
    }

    /**
     * Autentica um user com base no username e pw.
     * @param username Nome de user.
     * @param password Palavra-passe do user.
     * @param onSuccess Callback a ser chamado em caso de sucesso.
     */

    fun authenticate(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val hashedPassword = hashPassword(password) // hash a password
                val documents = db.collection("user")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", hashedPassword)
                    .get()
                    .await()

                if (documents.size() > 0) {
                    _result.value = "Authentication successful!" // Mensagem de sucesso
                    _isAuthenticated.value = true

                    // Atualiza os dados do utilizador
                    val document = documents.documents.first()
                    val userId = document.id
                    val fetchedUsername = document.getString("username") ?: "N/A"
                    val fetchedEmail = document.getString("email") ?: "N/A"

                    _userData.postValue(UserData(fetchedUsername, fetchedEmail)) // Atualiza o LiveData
                    auth.signInWithEmailAndPassword(document.getString("email") ?: "", password).await()

                    println(" pok -> Username: $fetchedUsername, Email: $fetchedEmail")
                    onSuccess() // Navega para o ecra principal
                } else {
                    _result.value = "Authentication failed."
                    _isAuthenticated.value = false
                }
            } catch (e: Exception) {
                _result.value = "Error: ${e.message}"
                _isAuthenticated.value = false
            }
        }
    }


    /**
     * Autentica o user com uma conta Google.
     * @param idToken Token de autenticação Google.
     * @param onSuccess Callback a ser chamado em caso de sucesso.
     */

    fun authenticateWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()

                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val userData = hashMapOf(
                        "uid" to user.uid,
                        "username" to (user.displayName ?: "N/A"),
                        "email" to (user.email ?: "N/A"),
                        "type" to "user" // Adiciona o campo type com valor "user" default
                    )

                    // Grava os dados no Firestore
                    db.collection("user").document(user.uid).set(userData, SetOptions.merge()).await()

                    _result.value = "Authentication with Google successful!"
                    _isAuthenticated.value = true
                    _userData.postValue(UserData(user.displayName ?: "N/A", user.email ?: "N/A"))
                    onSuccess()
                }
            } catch (e: Exception) {
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
     * @param onSuccess Callback a ser chamado em caso de sucesso.
     */
    fun registerNewUser(username: String, password: String, email: String, onSuccess: () -> Unit) {
        val hashedPassword = hashPassword(password) // hash password
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "password" to hashedPassword,
                    "type" to "user" // Adiciona o campo type com valor "user" por default
                )
                db.collection("user").document(auth.currentUser?.uid ?: "").set(user)
                    .addOnSuccessListener {
                        _result.value = "Registration successful!" // Sucesso
                        _isRegistered.value = true
                        onSuccess()
                    }
                    .addOnFailureListener {
                        _result.value = "Registration failed: ${it.message}"
                        _isRegistered.value = false
                    }
            } else {
                _result.value = "Registration failed: ${task.exception?.message}"
                _isRegistered.value = false
            }
        }
    }


    /**
     * Hash para password SHA-256.
     * @param password pw.
     * @return Palavra-passe já encriptada.
     */

    /**
     * Obtém os dados do utilizador autenticado a partir do Firestore.
     */

    // Classe para armazenar os dados do user
    data class UserData(
        val username: String,
        val email: String,
        val type: String = "user" // Campo type adicionado com default * isto está hardcoded, de outra maneira rebentava logo *
    )


    // Estado exposto para os dados do users
    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256") // Instância do SHA-256
        val hash = md.digest(password.toByteArray()) // Hash os bytes da palavra-passe
        return hash.joinToString("") { "%02x".format(it) } // Retorna em formato hexadecimal
    }

    fun getCurrentUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            viewModelScope.launch {
                try {
                    val snapshot = db.collection("user").document(userId).get().await()
                    val username = snapshot.getString("username") ?: "N/A"
                    val email = snapshot.getString("email") ?: "N/A"
                    val type = snapshot.getString("type") ?: "user" // procura o campo type

                    // Atualiza o StateFlow com os dados do user
                    _userData.value = UserData(username, email, type)
                } catch (e: Exception) {
                    _result.value = "Erro ao procurar dados: ${e.message}"
                }
            }
        } else {
            _result.value = "Utilizador não autenticado."
        }
    }


    fun logout() {
        auth.signOut() // Desloga o utilizador no Firebase
        _userData.postValue(UserData("N/A", "N/A")) // Limpa os dados do user
        _isAuthenticated.value = false // Reseta o estado de autenticação
        _result.value = "" // Limpa mensagens de resultado
    }

}