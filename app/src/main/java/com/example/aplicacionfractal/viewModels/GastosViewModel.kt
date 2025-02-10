package com.example.aplicacionfractal.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.FacturaDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GastosViewModel : ViewModel() {

    private val facturaDao = FacturaDao(FirebaseFirestore.getInstance())

    // Estado observable para ganancias y pérdidas
    private val _gananciasPerdidas = MutableStateFlow<Pair<Double, Double>>(0.0 to 0.0)
    val gananciasPerdidas: StateFlow<Pair<Double, Double>> = _gananciasPerdidas

    init {
        cargarGananciasYPerdidas()
    }

    // Método para cargar las ganancias y pérdidas desde Firestore
    private fun cargarGananciasYPerdidas() {
        viewModelScope.launch {
            try {
                val (ganancias, perdidas) = facturaDao.calcularGananciasYPerdidas()
                _gananciasPerdidas.value = ganancias to perdidas
            } catch (e: Exception) {
                // Manejo de errores (puedes agregar logs o mostrar un mensaje de error)
                e.printStackTrace()
            }
        }
    }
}
