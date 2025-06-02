package com.example.planifyapp.Auxiliar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificacionHelper(private val context: Context) {

    private val canalId = "canal_eventos"

    init {
        crearCanal()
    }

    private fun crearCanal() {
        val canal = NotificationChannel(
            canalId,
            "Eventos y Rutinas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de eventos importantes y rutinas diarias"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    fun mostrarNotificacion(titulo: String, mensaje: String) {
        val notificacion = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Comprobar permiso antes de notificar
        if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notificacion)
        }
    }
}