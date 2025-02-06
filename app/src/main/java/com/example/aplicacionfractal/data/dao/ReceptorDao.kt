package com.example.aplicacionfractal.data.dao

import com.example.aplicacionfractal.data.models.Receptor
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReceptorDao(private val db: FirebaseFirestore) {

    private val receptoresRef = db.collection("Receptor")

    suspend fun obtenerReceptores(): List<Receptor> {
        val querySnapshot = receptoresRef.get().await()
        return querySnapshot.documents.map { it.toObject(Receptor::class.java)!! }
    }

    suspend fun obtenerReceptor(id: String): Receptor? {
        val documentSnapshot = receptoresRef.document(id).get().await()
        return documentSnapshot.toObject(Receptor::class.java)
    }

    suspend fun agregarReceptor(receptor: Receptor) {
        //receptoresRef.add(receptor).await()
        receptoresRef.document(receptor.cif).set(receptor).await()
    }

    suspend fun actualizarReceptor(id: String, receptor: Receptor) {
        receptoresRef.document(id).set(receptor).await()
    }

    suspend fun borrarReceptor(id: String) {
        receptoresRef.document(id).delete().await()
    }

    fun obtenerReceptorId(id: String): DocumentReference? {
        return receptoresRef.document(id)
    }
}
