package com.example.aplicacionfractal.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionfractal.data.dao.EmisorDao
import com.example.aplicacionfractal.data.models.Emisor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmisorViewModel : ViewModel() {
    private val emisorDao = EmisorDao(FirebaseFirestore.getInstance())
    private val _emisores = MutableStateFlow<List<Emisor>>(emptyList())
    val emisores: StateFlow<List<Emisor>> = _emisores

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

//    init {
//        cargarEmisores()
//    }

//    private fun cargarEmisores() {
//        viewModelScope.launch {
//            try {
//                val emisoresObtenidos = emisorDao.obtenerEmisores()
//                _emisores.value = emisoresObtenidos.sortedByDescending { it.nif }
//                _error.value = null
//            } catch (e: Exception) {
//                _error.value = "Error cargando emisores: ${e.localizedMessage}"
//                Log.e("EmisorVM", "Error al cargar emisores", e)
//            }
//        }
//    }

    fun agregarEmisor(emisor: Emisor) = viewModelScope.launch {
        viewModelScope.launch {
            try {
                emisorDao.agregarEmisor(emisor)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error agregando emisor: ${e.localizedMessage}"
                Log.e("emisorVM", "Error al agregar emisor", e)
            }
        }
    }
}
