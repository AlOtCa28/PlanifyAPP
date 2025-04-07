package com.example.planifyapp.Auxiliar

import Modelo.EventoImportante.EventoImportante
import Modelo.Rutina.Rutina
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificacionHelper {

    fun programarNotificacionEvento(context: Context, evento: EventoImportante) {
        val diasAntes = 7
        val fechaNotificacion = evento.fechaEvento - TimeUnit.DAYS.toMillis(diasAntes.toLong())
        val retraso = fechaNotificacion - System.currentTimeMillis()

        if (retraso <= 0) return // No se programa si ya ha pasado la fecha

        val data = workDataOf(
            "titulo" to "Próximo evento: ${evento.titulo}",
            "mensaje" to evento.descripcion,
            "id" to evento.id.hashCode()
        )

        val notificacionRequest = OneTimeWorkRequestBuilder<NotificacionWorker>()
            .setInitialDelay(retraso, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(notificacionRequest)
    }

    fun programarNotificacionDiaria(context: Context, rutina: Rutina) {
        val hora = rutina.horaNotificacion.split(":").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora[0])
            set(Calendar.MINUTE, hora[1])
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }

        val retrasoInicial = calendar.timeInMillis - System.currentTimeMillis()

        val data = workDataOf(
            "titulo" to "Planificación diaria",
            "mensaje" to "Revisa tu rutina: ${rutina.titulo}",
            "id" to rutina.id.hashCode()
        )

        val notificacionDiaria = PeriodicWorkRequestBuilder<NotificacionWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(retrasoInicial, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(notificacionDiaria)
    }


    fun crearCanalNotificaciones(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "canal_eventos",
                "Eventos y Rutinas",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

}