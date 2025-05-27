package Modelo.TareasYLogros

import java.io.Serializable

data class TareaGamificada(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val puntos: Int,
    val completada: Boolean
): Serializable
