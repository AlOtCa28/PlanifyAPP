package Modelo.TareasYLogros

import java.io.Serializable

data class LogroGamificado(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var puntos: Int = 0,
    var obtenido: Boolean = false
): Serializable
