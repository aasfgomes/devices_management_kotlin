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

class UserViewModel : ViewModel() {

    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    private val _result = mutableStateOf("")
    val result: State<String> = _result

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    // Função para atualizar a mensagem de resultado
    fun updateResultMessage(message: String) {
        _result.value = message
    }

    fun authenticate(username: String, password: String) {
        viewModelScope.launch {
            try {
                val hashedPassword = hashPassword(password)
                val documents = db.collection("user")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", hashedPassword)
                    .get()
                    .await()
                if (documents.size() > 0) {
                    _result.value = "Authentication successful!"
                    _isAuthenticated.value = true
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

    fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _result.value = "Authentication with Google successful!"
                _isAuthenticated.value = true

                val user = auth.currentUser
                user?.let {
                    val userData = hashMapOf(
                        "uid" to it.uid,
                        "username" to it.displayName,
                        "email" to it.email
                    )
                    db.collection("user").document(it.uid).set(userData).await()
                }
            } catch (e: Exception) {
                _result.value = "Google Authentication failed: ${e.message}"
                _isAuthenticated.value = false
            }
        }
    }

    // Função para registar novos users
    fun registerNewUser(username: String, password: String, email: String) {
        val hashedPassword = hashPassword(password)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Armazena o user na firebase
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "password" to hashedPassword
                    )
                    db.collection("user").document(auth.currentUser?.uid ?: "").set(user)
                        .addOnSuccessListener {
                            _result.value = "Registration successful!"
                        }
                        .addOnFailureListener {
                            _result.value = "Registration failed: ${it.message}"
                        }
                } else {
                    _result.value = "Registration failed: ${task.exception?.message}"
                }
            }
    }

    // Função para hashear a password com SHA256
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}
