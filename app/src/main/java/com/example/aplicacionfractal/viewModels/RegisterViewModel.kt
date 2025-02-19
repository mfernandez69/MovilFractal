package com.example.aplicacionfractal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.UsuarioDao
import com.example.aplicacionfractal.data.models.Usuario
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(private val usuarioDao: UsuarioDao = UsuarioDao(FirebaseFirestore.getInstance())) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String, adminKey: String): Boolean {
        val emailFormateado = email.trim()

        return try {
            val isAdmin = if (adminKey.isNotEmpty()) {
                usuarioDao.verificarClaveAdmin(adminKey)
            } else false

            if (adminKey.isNotEmpty() && !isAdmin) {
                // La clave de administrador se proporcionó pero no es válida
                return false
            }

            val authResult = auth.createUserWithEmailAndPassword(emailFormateado, password).await()
            val userId = authResult.user?.uid ?: return false

            val usuario = Usuario(
                email = emailFormateado,
                role = if (isAdmin) "admin" else "user",
                createdAt = Timestamp.now()
            )
            usuarioDao.agregarUsuario(userId, usuario)
            true
        } catch (e: Exception) {
            // Manejar errores de registro
            false
        }
    }

}


