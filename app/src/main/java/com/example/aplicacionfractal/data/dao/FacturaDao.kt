package com.example.aplicacionfractal.data.dao

import android.util.Log
import com.example.aplicacionfractal.data.models.Factura
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FacturaDao(private val db: FirebaseFirestore) {

    private val facturasRef = db.collection("Factura")

    suspend fun obtenerFacturas(): List<Factura> {
        val querySnapshot = facturasRef.get().await()
        Log.d("Firestore", "Documentos obtenidos: ${querySnapshot.documents.size}")

        return querySnapshot.documents.mapNotNull { document ->
            try {
                document.toObject(Factura::class.java)
            } catch (e: Exception) {
                Log.e("Firestore", "Error al deserializar documento ${document.id}: ${e.message}")
                null
            }
        }
    }


    suspend fun obtenerFactura(id: String): Factura? {
        val documentSnapshot = facturasRef.document(id).get().await()
        return documentSnapshot.toObject(Factura::class.java)
    }
    suspend fun obtenerIdPorNfactura(nFactura: Int): String? {
        // Realiza una consulta para encontrar el documento con el nFactura dado
        val querySnapshot = facturasRef
            .whereEqualTo("nFactura", nFactura)
            .get()
            .await()

        // Verifica si la consulta devolvió algún documento
        if (!querySnapshot.isEmpty) {
            // Obtén el ID del primer documento encontrado
            return querySnapshot.documents.first().id
        }

        // Retorna null si no se encuentra ningún documento
        return null
    }


    suspend fun agregarFactura(factura: Factura) {
        facturasRef.add(factura).await()
    }

    suspend fun actualizarFactura(id: String, factura: Factura) {
        facturasRef.document(id).set(factura).await()
    }

    suspend fun borrarFactura(nFactura: Int) {
        try {
            // Busca el documento con el campo nFactura igual al valor proporcionado
            val querySnapshot = facturasRef.whereEqualTo("nFactura", nFactura).get().await()
            if (querySnapshot.documents.isNotEmpty()) {
                for (document in querySnapshot.documents) {
                    document.reference.delete().await() // Elimina cada documento encontrado
                    Log.d("Firestore", "Factura con nFactura $nFactura eliminada")
                }
            } else {
                Log.d("Firestore", "No se encontró ninguna factura con nFactura $nFactura")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al eliminar factura con nFactura $nFactura: ${e.message}")
        }
    }
    //calcular ganancias y pérdidas
    suspend fun calcularGananciasYPerdidas(): Pair<Double, Double> {
        try {
            // Obtener todas las facturas
            val querySnapshot = facturasRef.get().await()
            val facturas = querySnapshot.documents.mapNotNull { it.toObject(Factura::class.java) }

            // Inicializar variables para ganancias y pérdidas
            var ganancias = 0.0
            var perdidas = 0.0

            // Iterar sobre las facturas y clasificar según si son emitidas o recibidas
            for (factura in facturas) {
                if (factura.emitida) {
                    ganancias += factura.baseImponible // Ganancia por facturas emitidas
                } else {
                    perdidas += factura.baseImponible // Pérdida por facturas recibidas
                }
            }

            Log.d("GananciasYPerdidas", "Ganancias: $ganancias, Pérdidas: $perdidas")
            return Pair(ganancias, perdidas)

        } catch (e: Exception) {
            Log.e("Firestore", "Error al calcular ganancias y pérdidas: ${e.message}")
            return Pair(0.0, 0.0)
        }
    }

    suspend fun obtenerFacturaPorNumero(numeroFactura: Int): Factura? {
        return try {
            val querySnapshot = facturasRef.whereEqualTo("nFactura", numeroFactura).get().await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].toObject(Factura::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener factura por número $numeroFactura: ${e.message}")
            null
        }
    }

}
