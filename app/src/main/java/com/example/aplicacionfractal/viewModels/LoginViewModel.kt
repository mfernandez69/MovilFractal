package com.example.aplicacionfractal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.UsuarioDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel( private val usuarioDao: UsuarioDao = UsuarioDao(FirebaseFirestore.getInstance())) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
            } catch (e: Exception) {
            }
        }
    }
}
