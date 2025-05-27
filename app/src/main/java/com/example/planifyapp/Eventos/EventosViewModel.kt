package com.example.planifyapp.Eventos

import Modelo.EventoImportante.EventoImportante
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventosViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()

    private val _eventos = MutableStateFlow<List<EventoImportante>>(emptyList())
    val eventos : StateFlow<List<EventoImportante>> = _eventos

    fun cargarEventos(emailUsuario: String) {
        db.collection("EventosImportantes")
            .whereEqualTo("emailUsuario", emailUsuario)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.mapNotNull { it.toObject(EventoImportante::class.java) }
                _eventos.value = lista
            }
            .addOnFailureListener {
                _eventos.value = emptyList()
            }
    }

    fun actualizarNotificacionEvento(evento: EventoImportante, nuevoEstado: Boolean) {
        val docId = evento.id
        db.collection("EventosImportantes").document(docId)
            .update("notificarUnaSemanaAntes", nuevoEstado)
            .addOnSuccessListener {
                cargarEventos(evento.emailUsuario)
            }
    }

    fun agregarEvento(evento: EventoImportante) {
        val docRef = db.collection("EventosImportantes").document()
        evento.id = docRef.id
        docRef.set(evento)
    }


    fun eliminarEvento(eventoId: String) {
        db.collection("EventosImportantes").document(eventoId).delete()
    }

}