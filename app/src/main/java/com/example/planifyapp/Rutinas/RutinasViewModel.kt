package com.example.planifyapp.Rutinas

import Modelo.Rutina.Rutina
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RutinasViewModel : ViewModel()  {
    private val db = FirebaseFirestore.getInstance()

    private val _rutinas = MutableStateFlow<List<Rutina>>(emptyList())
    val rutinas: StateFlow<List<Rutina>> = _rutinas

    fun cargarRutinas(emailUsuario: String) {
        viewModelScope.launch {
            db.collection("Rutinas")
                .whereEqualTo("emailUsuario", emailUsuario)
                .get()
                .addOnSuccessListener { resultado ->
                    val lista = resultado.mapNotNull { it.toObject(Rutina::class.java) }
                    _rutinas.value = lista
                }
                .addOnFailureListener {
                    _rutinas.value = emptyList()
                }
        }
    }

    fun agregarRutina(rutina: Rutina) {
        db.collection("Rutinas").add(rutina)
    }

    fun eliminarRutina(rutinaId: String) {
        db.collection("Rutinas").document(rutinaId).delete()
    }
}