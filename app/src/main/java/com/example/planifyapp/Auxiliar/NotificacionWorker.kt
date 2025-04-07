package com.example.planifyapp.Auxiliar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificacionWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val titulo = inputData.getString("titulo") ?: "Evento"
        val mensaje = inputData.getString("mensaje") ?: "Tienes un evento programado."
        val id = inputData.getInt("id", 1)

        mostrarNotificacion(id, titulo, mensaje)
        return Result.success()
    }

    private fun mostrarNotificacion(id: Int, titulo: String, mensaje: String) {
        val canalId = "canal_eventos"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                canalId,
                "Eventos y Rutinas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de eventos importantes y rutinas diarias"
            }

            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(applicationContext, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

//        NotificationManagerCompat.from(applicationContext).notify(id, notificacion)
    }
}