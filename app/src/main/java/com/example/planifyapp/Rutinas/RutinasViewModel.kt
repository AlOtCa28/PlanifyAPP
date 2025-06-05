package com.example.planifyapp.Rutinas

import Modelo.Rutina.Rutina
import Modelo.Rutina.Tarea
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makefriendsapp.Auxiliar.Parametros
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RutinasViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Rutinas
    private val _rutinas = MutableStateFlow<List<Rutina>>(emptyList())
    val rutinas: StateFlow<List<Rutina>> = _rutinas

    // Tareas de la rutina seleccionada
    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> = _tareas

    private val _rutinaSeleccionada = MutableStateFlow<Rutina?>(null)
    val rutinaSeleccionada: StateFlow<Rutina?> = _rutinaSeleccionada

    private var emailUsuario: String = ""

    fun cargarRutinaPorId(rutinaId: String) {
        db.collection("Rutinas").document(rutinaId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val rutina = doc.toObject(Rutina::class.java)?.copy(id = doc.id)
                    _rutinaSeleccionada.value = rutina
                }
            }
            .addOnFailureListener {
                _rutinaSeleccionada.value = null
            }
    }

    // --- Rutinas ---

    fun cargarRutinas(emailUsuario: String) {
        db.collection("Rutinas")
            .whereEqualTo("emailUsuario", emailUsuario)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.mapNotNull { doc ->
                    val rutina = doc.toObject(Rutina::class.java).copy(id = doc.id)
                    rutina
                }
                _rutinas.value = lista
            }
            .addOnFailureListener {
                _rutinas.value = emptyList()
            }
    }

    fun actualizarEstadoRutina(rutina: Rutina, nuevoEstado: Boolean) {
        if (rutina.id.isNotEmpty()) {
            db.collection("Rutinas").document(rutina.id)
                .update("esActiva", nuevoEstado)
        }
    }

    fun agregarRutina(rutina: Rutina) {
        db.collection("Rutinas")
            .add(rutina)
            .addOnSuccessListener { docRef ->
                db.collection("Rutinas").document(docRef.id).update("id", docRef.id)
            }
    }

    fun eliminarRutina(rutinaId: String) {
        db.collection("Rutinas").document(rutinaId).delete()
    }

    // --- Tareas ---

    // En tu ViewModel
    fun cargarTareas(rutinaId: String) {
        db.collection("Rutinas")
            .document(rutinaId)
            .collection("Tareas")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { it.toObject(Tarea::class.java)?.copy(id = it.id) }
                    _tareas.value = lista
                }
            }
    }

    fun agregarTarea(rutinaId: String, tarea: Tarea, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val tareasCollection = db.collection("Rutinas").document(rutinaId).collection("Tareas")
        val docId = tareasCollection.document().id
        val tareaConId = tarea.copy(id = docId)

        tareasCollection.document(docId).set(tareaConId)
            .addOnSuccessListener { onComplete(true, docId) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun marcarTareaCompletada(
        tarea: Tarea,
        completada: Boolean,
        onCompletado: () -> Unit = {}
    ) {
        val rutina = _rutinaSeleccionada.value ?: return
        val email = Parametros.usuarioLogged?.correo ?: return

        val tareaRef = db.collection("Rutinas")
            .document(rutina.id)
            .collection("Tareas")
            .document(tarea.id)

        tareaRef.update("completada", completada).addOnSuccessListener {
            cargarTareas(rutina.id)

            if (completada) {
                val progresoRef = db.collection("ProgresoUsuarios")
                    .document(email)
                    .collection("TareasRutinaCompletadas")
                    .document(tarea.id)

                progresoRef.get().addOnSuccessListener { docSnapshot ->
                    if (!docSnapshot.exists()) {
                        progresoRef.set(
                            mapOf(
                                "id" to tarea.id,
                                "titulo" to tarea.titulo,
                                "descripcion" to tarea.descripcion,
                                "puntos" to tarea.puntos,
                                "completada" to true,
                                "fechaCompletada" to FieldValue.serverTimestamp()
                            )
                        ).addOnSuccessListener {
                            val usuarioRef = db.collection("Usuarios").document(email)
                            usuarioRef.update("puntos", FieldValue.increment(tarea.puntos.toLong()))
                                .addOnSuccessListener {
                                    onCompletado()
                                }
                        }
                    }
                }
            }
        }
    }


    fun setEmailUsuario(email: String) {
        emailUsuario = email
    }
}