package com.example.aplicacionfractal.data.dao

import com.example.aplicacionfractal.data.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioDao(private val db: FirebaseFirestore) {
    private val usuariosRef = db.collection("Usuario")

    suspend fun obtenerUsuario(uid: String): Usuario? {
        val documentSnapshot = usuariosRef.document(uid).get().await()
        return documentSnapshot.toObject(Usuario::class.java)
    }

    suspend fun agregarUsuario(uid: String, usuario: Usuario) {
        usuariosRef.document(uid).set(usuario).await()
    }

    suspend fun actualizarUsuario(uid: String, usuario: Usuario) {
        usuariosRef.document(uid).set(usuario).await()
    }

    suspend fun verificarClaveAdmin(clave: String): Boolean {
        val documentSnapshot = db.collection("AdminKeys").document(clave).get().await()
        return documentSnapshot.exists()
    }
}
