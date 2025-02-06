package com.example.aplicacionfractal.data.dao

import com.example.aplicacionfractal.data.models.Emisor
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmisorDao(private val db: FirebaseFirestore) {

    private val emisoresRef = db.collection("Emisor")

    suspend fun obtenerEmisores(): List<Emisor> {
        val querySnapshot = emisoresRef.get().await()
        return querySnapshot.documents.map { it.toObject(Emisor::class.java)!! }
    }

    suspend fun obtenerEmisor(id: String): Emisor? {
        val documentSnapshot = emisoresRef.document(id).get().await()
        return documentSnapshot.toObject(Emisor::class.java)
    }

    suspend fun agregarEmisor(emisor: Emisor) {
        //emisoresRef.add(emisor).await()
        emisoresRef.document(emisor.nif).set(emisor).await()
    }

    suspend fun actualizarEmisor(id: String, emisor: Emisor) {
        emisoresRef.document(id).set(emisor).await()
    }

    suspend fun borrarEmisor(id: String) {
        emisoresRef.document(id).delete().await()
    }

    fun obtenerEmisorId(id: String): DocumentReference? {
        return emisoresRef.document(id)
    }
}
