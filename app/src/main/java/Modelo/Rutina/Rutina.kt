package Modelo.Rutina

import java.io.Serializable

data class Rutina(
    var id: String = "",
    var idUsuario: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var horaNotificacion: String = "",       // Ej: "08:30"
    var diasRepeticion: List<String> = listOf(),  // Ej: ["Lunes", "Miércoles"]
    var esActiva: Boolean = true
) : Serializable

