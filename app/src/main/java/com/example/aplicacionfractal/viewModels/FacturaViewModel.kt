package com.example.aplicacionfractal.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.EmisorDao
import com.example.aplicacionfractal.data.dao.FacturaDao
import com.example.aplicacionfractal.data.dao.ReceptorDao
import com.example.aplicacionfractal.data.models.Emisor
import com.example.aplicacionfractal.data.models.Factura
import com.example.aplicacionfractal.data.models.Receptor
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FacturaViewModel : ViewModel() {
    private val facturaDao = FacturaDao(FirebaseFirestore.getInstance())
    private val emisorDao = EmisorDao(FirebaseFirestore.getInstance())
    private val receptorDao = ReceptorDao(FirebaseFirestore.getInstance())
    private val _facturas = MutableStateFlow<List<Factura>>(emptyList())
    private val _facturaActual = MutableStateFlow<Factura?>(null)
    val facturas: StateFlow<List<Factura>> = _facturas

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarFacturas()
    }

    private fun cargarFacturas() {
        viewModelScope.launch {
            try {
                val facturasObtenidas = facturaDao.obtenerFacturas()
                _facturas.value = facturasObtenidas.sortedByDescending { it.fechaEmision }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando facturas: ${e.localizedMessage}"
                Log.e("FacturaVM", "Error al cargar facturas", e)
            }
        }
    }

    fun agregarFactura(factura: Factura, emisor: Emisor, receptor: Receptor) = viewModelScope.launch {
        factura.emisorId = emisorDao.obtenerEmisorId(emisor.nif)
        factura.receptorId = receptorDao.obtenerReceptorId(receptor.cif)
        viewModelScope.launch {
            try {
                facturaDao.agregarFactura(factura)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error agregando factura: ${e.localizedMessage}"
                Log.e("FacturaVM", "Error al agregar factura", e)
            }
        }
    }
    fun eliminarFactura(factura: Factura) = viewModelScope.launch {
        try {
            factura.nFactura.let { facturaDao.borrarFactura(it) }
            _facturas.value = _facturas.value.filter { it.nFactura != factura.nFactura }
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error eliminando factura: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al eliminar factura", e)
        }
    }
    suspend fun obtenerEmisor(documentReference: DocumentReference?): Emisor? {
        return try {
            documentReference?.let { emisorDao.obtenerEmisor(it.id) }
        } catch (e: Exception) {
            _error.value = "Error obteniendo emisor: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al obtener emisor", e)
            null
        }
    }

    suspend fun obtenerReceptor(documentReference: DocumentReference?): Receptor? {
        return try {
            documentReference?.let { receptorDao.obtenerReceptor(it.id) }
        } catch (e: Exception) {
            _error.value = "Error obteniendo receptor: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al obtener receptor", e)
            null
        }
    }

    fun renovarFactura(factura: Factura) = viewModelScope.launch {
        try {
            factura.nFactura.let { facturaDao.actualizarFactura("",factura) }
            _facturas.value = _facturas.value.filter { it.nFactura != factura.nFactura }
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error eliminando factura: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al actualizar factura", e)
        }
    }

    suspend fun obtenerFactura(documentReference: DocumentReference?): Factura? {
        return try {
            documentReference?.let { facturaDao.obtenerFactura(it.id) }
        } catch (e: Exception) {
            _error.value = "Error obteniendo factura: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al obtener factura", e)
            null
        }
    }
    suspend fun obtenerFacturaPorNumero(numeroFactura: Int): Factura? {
        return try {
            val factura = facturaDao.obtenerFacturaPorNumero(numeroFactura)
            _facturaActual.value = factura
            factura
        } catch (e: Exception) {
            _error.value = "Error obteniendo factura: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al obtener factura por número", e)
            null
        }
    }
    suspend fun obtenerEmisorPorId(id: String): Emisor? {
        return emisorDao.obtenerEmisor(id)
    }

    suspend fun obtenerReceptorPorId(id: String): Receptor? {
        return receptorDao.obtenerReceptor(id)
    }
    suspend fun actualizarFactura(factura: Factura, emisor: Emisor, receptor: Receptor) {
        try {
            val emisorId = emisorDao.agregarOActualizarEmisor(emisor)
            val receptorId = receptorDao.agregarOActualizarReceptor(receptor)

            val facturaActualizada = factura.copy(
                emisorId = emisorId,
                receptorId = receptorId
            )
            val idFactura = facturaDao.obtenerIdPorNfactura(factura.nFactura)
            if (idFactura != null) {
                facturaDao.actualizarFactura(idFactura, facturaActualizada)
            }
            _facturas.value = _facturas.value.map { if (it.nFactura == factura.nFactura) facturaActualizada else it }
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error actualizando factura: ${e.localizedMessage}"
            Log.e("FacturaVM", "Error al actualizar factura", e)
        }
    }

}
