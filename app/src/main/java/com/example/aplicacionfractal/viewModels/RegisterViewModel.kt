package com.example.aplicacionfractal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.UsuarioDao
import com.example.aplicacionfractal.data.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(private val usuarioDao: UsuarioDao = UsuarioDao(FirebaseFirestore.getInstance())) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun register(email: String, password: String, adminKey: String) {
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: return@launch

                val isAdmin = if (adminKey.isNotEmpty()) {
                    usuarioDao.verificarClaveAdmin(adminKey)
                } else false

                val usuario = Usuario(
                    email = email,
                    role = if (isAdmin) "admin" else "user"
                )
                usuarioDao.agregarUsuario(userId, usuario)
            } catch (e: Exception) {
                // Manejar errores de registro
            }
        }
    }
}

