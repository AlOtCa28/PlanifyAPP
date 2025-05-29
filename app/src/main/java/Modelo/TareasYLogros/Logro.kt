package Modelo.TareasYLogros

import java.io.Serializable

data class Logro(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var puntos: Int = 0
) :Serializable
