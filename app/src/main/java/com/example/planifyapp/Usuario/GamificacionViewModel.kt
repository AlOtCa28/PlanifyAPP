package com.example.planifyapp.Usuario

import Modelo.TareasYLogros.LogroGamificado
import Modelo.TareasYLogros.TareaGamificada
import Modelo.TareasYLogros.TareaGeneral
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


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

            val tareasGenerales = tareasSnapshot.documents.mapNotNull { doc ->
                doc.toObject(TareaGeneral::class.java)?.apply { id = doc.id }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun descargarImagenDesdeFirebase(correo: String): Bitmap? = suspendCancellableCoroutine { continuation ->
        val localFile = File.createTempFile("tempImage", "jpeg")

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("imagenes/$correo")

        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            continuation.resume(bitmap)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }

}
