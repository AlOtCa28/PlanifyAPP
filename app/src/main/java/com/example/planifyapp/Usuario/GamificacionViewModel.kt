package com.example.planifyapp.Usuario

import Modelo.TareasYLogros.LogroGamificado
import Modelo.TareasYLogros.TareaGamificada
import Modelo.TareasYLogros.TareaGeneral
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



class GamificacionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _tareas = MutableStateFlow<List<TareaGamificada>>(emptyList())
    val tareas: StateFlow<List<TareaGamificada>> = _tareas

    private val _logros = MutableStateFlow<List<LogroGamificado>>(emptyList())
    val logros: StateFlow<List<LogroGamificado>> = _logros

    private val _puntosTotales = MutableStateFlow(0)
    val puntosTotales: StateFlow<Int> = _puntosTotales

    fun cargarDatos(emailUsuario: String) {
        // Cargar tareas generales
        db.collection("TareasGenerales").get().addOnSuccessListener { tareasSnapshot ->

            val tareasGenerales = tareasSnapshot.documents.map { doc ->
                val tarea = doc.toObject(TareaGeneral::class.java)
                tarea?.id = doc.id
                tarea!!
            }

            // Cargar progreso de tareas del usuario
            db.collection("ProgresoUsuarios")
                .document(emailUsuario)
                .collection("TareasCompletadas")
                .get()
                .addOnSuccessListener { progresoTareasSnapshot ->

                    val progresoTareasMap = progresoTareasSnapshot.documents.associate { doc ->
                        doc.id to (doc.getBoolean("completada") ?: false)
                    }

                    // Cargar logros generales
                    db.collection("LogrosGenerales").get().addOnSuccessListener { logrosSnapshot ->

                        val logrosGenerales = logrosSnapshot.documents.map { doc ->
                            val logro = doc.toObject(LogroGamificado::class.java)
                            logro?.id = doc.id
                            logro!!
                        }

                        // Cargar progreso de logros del usuario
                        db.collection("ProgresoUsuarios")
                            .document(emailUsuario)
                            .collection("LogrosObtenidos")
                            .get()
                            .addOnSuccessListener { progresoLogrosSnapshot ->

                                val progresoLogrosMap = progresoLogrosSnapshot.documents.associate { doc ->
                                    doc.id to (doc.getBoolean("obtenido") ?: false)
                                }

                                // Combinar tareas con progreso
                                val listaTareasFinal = tareasGenerales.map { tarea ->
                                    TareaGamificada(
                                        id = tarea.id,
                                        titulo = tarea.titulo,
                                        descripcion = tarea.descripcion,
                                        puntos = tarea.puntos,
                                        completada = progresoTareasMap[tarea.id] ?: false
                                    )
                                }

                                // Combinar logros con progreso
                                val listaLogrosFinal = logrosGenerales.map { logro ->
                                    LogroGamificado(
                                        id = logro.id,
                                        titulo = logro.titulo,
                                        descripcion = logro.descripcion,
                                        puntos = logro.puntos,
                                        obtenido = progresoLogrosMap[logro.id] ?: false
                                    )
                                }

                                // Actualizar estados
                                _tareas.value = listaTareasFinal
                                _logros.value = listaLogrosFinal

                                // Calcular puntos totales sumando tareas y logros obtenidos
                                val puntos = listaTareasFinal.filter { it.completada }.sumOf { it.puntos } +
                                        listaLogrosFinal.filter { it.obtenido }.sumOf { it.puntos }
                                _puntosTotales.value = puntos
                            }
                    }
                }
        }
    }

    fun completarTarea(emailUsuario: String, idTarea: String) {
        db.collection("ProgresoUsuarios")
            .document(emailUsuario)
            .collection("TareasCompletadas")
            .document(idTarea)
            .set(
                mapOf(
                    "completada" to true,
                    "fechaCompletada" to FieldValue.serverTimestamp()
                )
            ).addOnSuccessListener {
                cargarDatos(emailUsuario)
            }
    }

    fun obtenerLogro(emailUsuario: String, idLogro: String) {
        db.collection("ProgresoUsuarios")
            .document(emailUsuario)
            .collection("LogrosObtenidos")
            .document(idLogro)
            .set(
                mapOf(
                    "obtenido" to true,
                    "fechaObtenido" to FieldValue.serverTimestamp()
                )
            ).addOnSuccessListener {
                cargarDatos(emailUsuario)
            }
    }

}
