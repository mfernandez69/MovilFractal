package com.example.aplicacionfractal.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.ReceptorDao
import com.example.aplicacionfractal.data.models.Receptor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReceptorViewModel : ViewModel() {
    private val receptorDao = ReceptorDao(FirebaseFirestore.getInstance())
    private val _receptores = MutableStateFlow<List<Receptor>>(emptyList())
    val receptores: StateFlow<List<Receptor>> = _receptores

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

//    init {
////        cargarReceptores()
////    }
////
////    private fun cargarReceptores() {
////        viewModelScope.launch {
////            try {
////                val receptoresObtenidos = receptorDao.obtenerReceptores()
////                _receptores.value = receptoresObtenidos.sortedByDescending { it.cif }
////                _error.value = null
////            } catch (e: Exception) {
////                _error.value = "Error cargando receptores: ${e.localizedMessage}"
////                Log.e("ReceptorVM", "Error al cargar receptores", e)
////            }
////        }
////    }

    fun agregarReceptor(receptor: Receptor) = viewModelScope.launch {
        viewModelScope.launch {
            try {
                receptorDao.agregarReceptor(receptor)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error agregando receptor: ${e.localizedMessage}"
                Log.e("receptorVM", "Error al agregar receptor", e)
            }
        }
    }
}
