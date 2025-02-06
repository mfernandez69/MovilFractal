package com.example.aplicacionfractal.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.FacturaDao
import com.example.aplicacionfractal.data.models.Factura
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FacturaViewModel : ViewModel() {
    private val facturaDao = FacturaDao(FirebaseFirestore.getInstance())
    private val _facturas = MutableStateFlow<List<Factura>>(emptyList())
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
}
