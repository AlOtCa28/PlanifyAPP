package Modelo.EventoImportante

import java.io.Serializable

data class EventoImportante(
    var id: String = "",
    var emailUsuario: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var fechaEvento: Long = 0L,
    var notificarUnaSemanaAntes: Boolean = true,
    var latitud: Double? = null,
    var longitud: Double? = null
) : Serializable
