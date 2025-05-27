package Modelo.Rutina

import java.io.Serializable

data class Tarea(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var puntos: Int = 0,
    var completada: Boolean = false
) : Serializable
