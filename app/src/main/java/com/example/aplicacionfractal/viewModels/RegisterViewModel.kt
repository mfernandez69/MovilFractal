package com.example.aplicacionfractal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.UsuarioDao
import com.example.aplicacionfractal.data.models.Usuario
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
sealed class RegisterResult {
    object Success : RegisterResult()
    object InvalidAdminKey : RegisterResult()
    data class OtherError(val message: String) : RegisterResult()
}

class RegisterViewModel(private val usuarioDao: UsuarioDao = UsuarioDao(FirebaseFirestore.getInstance())) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String, adminKey: String): RegisterResult {
        val emailFormateado = email.trim()

        return try {
            val isAdmin = if (adminKey.isNotEmpty()) {
                usuarioDao.verificarClaveAdmin(adminKey)
            } else false

            if (adminKey.isNotEmpty() && !isAdmin) {
                return RegisterResult.InvalidAdminKey
            }

            val authResult = auth.createUserWithEmailAndPassword(emailFormateado, password).await()
            val userId = authResult.user?.uid ?: return RegisterResult.OtherError("Error al crear usuario")

            val usuario = Usuario(
                email = emailFormateado,
                role = if (isAdmin) "admin" else "user",
                createdAt = Timestamp.now()
            )
            usuarioDao.agregarUsuario(userId, usuario)
            RegisterResult.Success
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthWeakPasswordException -> RegisterResult.OtherError("La contraseña es demasiado débil")
                is FirebaseAuthInvalidCredentialsException -> RegisterResult.OtherError("Email inválido")
                is FirebaseAuthUserCollisionException -> RegisterResult.OtherError("El email ya está registrado")
                else -> RegisterResult.OtherError("Error de registro: ${e.message}")
            }
        }
    }
}


