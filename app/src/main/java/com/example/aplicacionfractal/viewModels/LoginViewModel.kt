package com.example.aplicacionfractal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.UsuarioDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(private val usuarioDao: UsuarioDao = UsuarioDao(FirebaseFirestore.getInstance())) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return false

            val usuario = usuarioDao.obtenerUsuario(userId)
            usuario?.role == "admin"
        } catch (e: Exception) {
            false
        }
    }
    fun logout(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }
}

