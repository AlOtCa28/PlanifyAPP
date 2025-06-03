package Modelo.TareasYLogros

import java.io.Serializable

data class LogroGamificado(
    var id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val puntos: Int = 0,
    var obtenido: Boolean = false,
    val fechaObtenido: com.google.firebase.Timestamp? = null,
    var tipo: TipoLogro = TipoLogro.GENERAL // Añadido campo tipo también
) : Serializable

