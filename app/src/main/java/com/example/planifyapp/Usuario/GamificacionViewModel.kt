package com.example.planifyapp.Usuario

import Auxiliar.Factorias.diaANombre
import Modelo.TareasYLogros.LogroGamificado
import Modelo.TareasYLogros.TareaGamificada
import Modelo.TareasYLogros.TareaGeneral
import Modelo.Rutina.Tarea // Asegúrate de tener este import o el correcto para tus tareas de rutina
import Modelo.Sugerencia.Sugerencia
import Modelo.TareasYLogros.Logro
import Modelo.TareasYLogros.TipoLogro
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Calendar
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

    private val _logroConseguidoEvento = MutableStateFlow<LogroGamificado?>(null)
    val logroConseguidoEvento = _logroConseguidoEvento.asStateFlow()

    private val _sugerencias = MutableStateFlow<List<Sugerencia>>(emptyList())
    val sugerencias: StateFlow<List<Sugerencia>> = _sugerencias

    // NUEVO: Para guardar tareas de rutina completadas
    private val _tareasRutina = MutableStateFlow<List<Tarea>>(emptyList())
    val tareasRutina: StateFlow<List<Tarea>> = _tareasRutina

    fun limpiarEvento() {
        _logroConseguidoEvento.value = null
    }


    fun cargarDatos(emailUsuario: String) {
        // Cargar tareas generales (igual que antes)
        db.collection("TareasGenerales").get().addOnSuccessListener { tareasSnapshot ->
            val tareasGenerales = tareasSnapshot.documents.mapNotNull { doc ->
                doc.toObject(TareaGeneral::class.java)?.apply { id = doc.id }
            }

            // Cargar progreso de tareas del usuario (igual que antes)
            db.collection("ProgresoUsuarios")
                .document(emailUsuario)
                .collection("TareasCompletadas")
                .get()
                .addOnSuccessListener { progresoTareasSnapshot ->

                    val puntosTareasCompletadas = progresoTareasSnapshot.documents
                        .filter { it.getBoolean("completada") == true }
                        .sumOf { it.getLong("puntos")?.toInt() ?: 0 }

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

                        // Cargar progreso completo de logros del usuario (con toda la info)
                        db.collection("ProgresoUsuarios")
                            .document(emailUsuario)
                            .collection("LogrosObtenidos")
                            .get()
                            .addOnSuccessListener { progresoLogrosSnapshot ->

                                val logrosObtenidos = progresoLogrosSnapshot.documents.mapNotNull { doc ->
                                    doc.toObject(LogroGamificado::class.java)?.apply { id = doc.id }
                                }

                                // Combinar logros generales con progreso obtenido
                                val listaLogrosFinal = logrosGenerales.map { logroGeneral ->
                                    val logroObtenido = logrosObtenidos.find { it.id == logroGeneral.id }
                                    if (logroObtenido != null) {
                                        logroObtenido
                                    } else {
                                        logroGeneral.copy(obtenido = false)
                                    }
                                }

                                // Cargar todas las rutinas del usuario (igual que antes)
                                db.collection("Rutinas")
                                    .whereEqualTo("emailUsuario", emailUsuario)
                                    .get()
                                    .addOnSuccessListener { rutinasSnapshot ->
                                        val rutinas = rutinasSnapshot.documents

                                        val todasTareasRutina = mutableListOf<Tarea>()
                                        var rutinasProcesadas = 0

                                        if (rutinas.isEmpty()) {
                                            _tareasRutina.value = emptyList()
                                            _tareas.value = tareasGenerales.map { tarea ->
                                                TareaGamificada(
                                                    id = tarea.id,
                                                    titulo = tarea.titulo,
                                                    descripcion = tarea.descripcion,
                                                    puntos = tarea.puntos,
                                                    completada = progresoTareasMap[tarea.id] ?: false
                                                )
                                            }
                                            _logros.value = listaLogrosFinal
                                            _puntosTotales.value = puntosTareasCompletadas +
                                                    listaLogrosFinal.filter { it.obtenido }.sumOf { it.puntos }
                                            return@addOnSuccessListener
                                        }

                                        for (rutinaDoc in rutinas) {
                                            rutinaDoc.reference.collection("Tareas")
                                                .get()
                                                .addOnSuccessListener { tareasSnapshot ->
                                                    tareasSnapshot.documents.mapNotNullTo(todasTareasRutina) { doc ->
                                                        doc.toObject(Tarea::class.java)?.apply { id = doc.id }
                                                    }
                                                    rutinasProcesadas++
                                                    if (rutinasProcesadas == rutinas.size) {
                                                        db.collection("ProgresoUsuarios")
                                                            .document(emailUsuario)
                                                            .collection("TareasRutinaCompletadas")
                                                            .get()
                                                            .addOnSuccessListener { tareasRutinaCompletadasSnapshot ->
                                                                val idsCompletadas = tareasRutinaCompletadasSnapshot.documents.map { it.id }.toSet()
                                                                val listaTareasRutinaFinal = todasTareasRutina.map { tarea ->
                                                                    tarea.copy(completada = idsCompletadas.contains(tarea.id))
                                                                }
                                                                _tareasRutina.value = listaTareasRutinaFinal

                                                                val puntos = puntosTareasCompletadas +
                                                                        listaLogrosFinal.filter { it.obtenido }.sumOf { it.puntos } +
                                                                        listaTareasRutinaFinal.filter { it.completada }.sumOf { it.puntos }
                                                                _tareas.value = tareasGenerales.map { tarea ->
                                                                    TareaGamificada(
                                                                        id = tarea.id,
                                                                        titulo = tarea.titulo,
                                                                        descripcion = tarea.descripcion,
                                                                        puntos = tarea.puntos,
                                                                        completada = progresoTareasMap[tarea.id] ?: false
                                                                    )
                                                                }
                                                                _logros.value = listaLogrosFinal
                                                                _puntosTotales.value = puntos
                                                            }
                                                    }
                                                }

                                        }
                                    }
                            }
                    }
                }
        }
    }


    fun generarSugerencias(emailUsuario: String) {
        db.collection("ProgresoUsuarios")
            .document(emailUsuario)
            .collection("TareasCompletadas")
            .get()
            .addOnSuccessListener { snapshot ->
                val nuevasSugerencias = mutableListOf<Sugerencia>()
                val diasContador = mutableMapOf<Int, Int>()
                val horasContador = mutableMapOf<Int, Int>()

                for (doc in snapshot.documents) {
                    val timestamp = doc.getTimestamp("fechaCompletada")?.toDate()
                    timestamp?.let {
                        val cal = Calendar.getInstance().apply { time = it }
                        val dia = cal.get(Calendar.DAY_OF_WEEK)
                        val hora = cal.get(Calendar.HOUR_OF_DAY)

                        diasContador[dia] = diasContador.getOrDefault(dia, 0) + 1
                        horasContador[hora] = horasContador.getOrDefault(hora, 0) + 1
                    }
                }

                val mejorDia = diasContador.maxByOrNull { it.value }?.key
                val mejorHora = horasContador.maxByOrNull { it.value }?.key

                mejorDia?.let {
                    nuevasSugerencias.add(
                        Sugerencia(
                            tipo = "rutina",
                            titulo = "Nueva rutina para tu día más productivo",
                            descripcion = "Te va bien trabajar ese día. Crea una rutina semanal ahí.",
                            motivo = "Basado en tus hábitos, el día ${diaANombre(it)} es donde más completas tareas."
                        )
                    )
                }

                mejorHora?.let {
                    nuevasSugerencias.add(
                        Sugerencia(
                            tipo = "tarea",
                            titulo = "Añade tareas a tu hora favorita",
                            descripcion = "Podrías planear más cosas para esa hora del día.",
                            motivo = "La hora ${it}:00 es cuando más tareas completas."
                        )
                    )
                }

                _sugerencias.value = nuevasSugerencias
            }
            .addOnFailureListener {
                _sugerencias.value = emptyList()
            }
    }

    fun completarTarea(emailUsuario: String, tarea: TareaGamificada) {
        db.collection("ProgresoUsuarios")
            .document(emailUsuario)
            .collection("TareasCompletadas")
            .document(tarea.id)
            .set(
                mapOf(
                    "id" to tarea.id,
                    "titulo" to tarea.titulo,
                    "descripcion" to tarea.descripcion,
                    "puntos" to tarea.puntos,
                    "completada" to true,
                    "fechaCompletada" to FieldValue.serverTimestamp()
                )
            )
            .addOnSuccessListener {
                comprobarLogros(emailUsuario)
                cargarDatos(emailUsuario)
            }
    }

    fun comprobarLogros(emailUsuario: String) {
        val usuarioRef = db.collection("ProgresoUsuarios").document(emailUsuario)

        // Obtener tareas completadas del tablón
        usuarioRef.collection("TareasCompletadas")
            .get()
            .addOnSuccessListener { tareasTablonSnapshot ->
                val tareasTablonCompletadas = tareasTablonSnapshot.documents.count { it.getBoolean("completada") == true }

                // Obtener tareas completadas de rutina
                usuarioRef.collection("TareasRutinaCompletadas")
                    .get()
                    .addOnSuccessListener { tareasRutinaSnapshot ->
                        val tareasRutinaCompletadas = tareasRutinaSnapshot.documents.count { it.getBoolean("completada") == true }

                        val totalTareasCompletadas = tareasTablonCompletadas + tareasRutinaCompletadas

                        // Obtener logros generales
                        db.collection("LogrosGenerales")
                            .get()
                            .addOnSuccessListener { logrosSnapshot ->

                                for (doc in logrosSnapshot.documents) {
                                    val logro = doc.toObject(Logro::class.java)?.apply { id = doc.id }

                                    if (logro != null && logro.tipo == TipoLogro.PUNTUAL) {
                                        if (logro.titulo.contains("SANGUINARIO") && totalTareasCompletadas >= 5) {
                                            usuarioRef.collection("LogrosObtenidos")
                                                .document(logro.id)
                                                .get()
                                                .addOnSuccessListener { docLogro ->
                                                    if (!docLogro.exists()) {
                                                        val logroGamificado = LogroGamificado(
                                                            id = logro.id,
                                                            titulo = logro.titulo,
                                                            descripcion = logro.descripcion,
                                                            puntos = logro.puntos,
                                                            obtenido = true,
                                                            fechaObtenido = com.google.firebase.Timestamp.now(),
                                                            tipo = logro.tipo
                                                        )
                                                        usuarioRef.collection("LogrosObtenidos")
                                                            .document(logro.id)
                                                            .set(logroGamificado)
                                                            .addOnSuccessListener {
                                                                _logroConseguidoEvento.value = logroGamificado
                                                                cargarDatos(emailUsuario)
                                                            }
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                    }
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
