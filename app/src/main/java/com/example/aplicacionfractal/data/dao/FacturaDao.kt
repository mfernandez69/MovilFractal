package com.example.aplicacionfractal.data.dao

import com.example.aplicacionfractal.data.models.Factura
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FacturaDao(private val db: FirebaseFirestore) {

    private val facturasRef = db.collection("Factura")

    suspend fun obtenerFacturas(): List<Factura> {
        val querySnapshot = facturasRef.get().await()
        return querySnapshot.documents.map { it.toObject(Factura::class.java)!! }
    }

    suspend fun obtenerFactura(id: String): Factura? {
        val documentSnapshot = facturasRef.document(id).get().await()
        return documentSnapshot.toObject(Factura::class.java)
    }

    suspend fun agregarFactura(factura: Factura) {
        facturasRef.add(factura).await()
    }

    suspend fun actualizarFactura(id: String, factura: Factura) {
        facturasRef.document(id).set(factura).await()
    }

    suspend fun borrarFactura(id: String) {
        facturasRef.document(id).delete().await()
    }
}
