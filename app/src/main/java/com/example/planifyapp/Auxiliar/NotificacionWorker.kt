package com.example.planifyapp.Auxiliar

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class NotificacionWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        val emailUsuario = inputData.getString("emailUsuario") ?: return Result.failure()
        val notificacionHelper = NotificacionHelper(applicationContext)

        try {
            // 1. Rutinas activas
            val rutinasTask = db.collection("Rutinas")
                .whereEqualTo("emailUsuario", emailUsuario)
                .whereEqualTo("esActiva", true)
                .get()
            val rutinasSnapshot = Tasks.await(rutinasTask)
            val hoy = obtenerDiaActual()
            for (rutina in rutinasSnapshot) {
                val dias = rutina.get("diasRepeticion") as? List<*> ?: emptyList<String>()
                val hora = rutina.getString("horaNotificacion") ?: continue
                if (dias.contains(hoy) && esHoraDeNotificar(hora)) {
                    notificacionHelper.mostrarNotificacion(
                        "Rutina: ${rutina.getString("titulo")}",
                        rutina.getString("descripcion") ?: ""
                    )
                }
            }

            // 2. Eventos importantes activos
            val eventosTask = db.collection("EventosImportantes")
                .whereEqualTo("emailUsuario", emailUsuario)
                .whereEqualTo("activo", true)
                .get()
            val eventosSnapshot = Tasks.await(eventosTask)
            for (evento in eventosSnapshot) {
                val fechaEvento = evento.getString("fecha")
                val horaEvento = evento.getString("hora")
                if (esMomentoDeNotificarEvento(fechaEvento, horaEvento)) {
                    notificacionHelper.mostrarNotificacion(
                        "Evento: ${evento.getString("titulo")}",
                        evento.getString("descripcion") ?: ""
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    // Ejemplo de funciones auxiliares:
    private fun obtenerDiaActual(): String {
        // Devuelve el día actual en el formato de tus rutinas, por ejemplo "Lunes"
        val dias = listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        val cal = java.util.Calendar.getInstance()
        return dias[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
    }

    @SuppressLint("SimpleDateFormat")
    private fun esHoraDeNotificar(hora: String): Boolean {
        // Compara la hora actual con la hora de la rutina (formato "HH:mm")
        val ahora = java.text.SimpleDateFormat("HH:mm").format(java.util.Date())
        return ahora == hora
    }

    @SuppressLint("SimpleDateFormat")
    private fun esMomentoDeNotificarEvento(fecha: String?, hora: String?): Boolean {
        // Compara fecha y hora del evento con la actual
        if (fecha == null || hora == null) return false
        val ahoraFecha = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
        val ahoraHora = java.text.SimpleDateFormat("HH:mm").format(java.util.Date())
        return fecha == ahoraFecha && hora == ahoraHora
    }
}