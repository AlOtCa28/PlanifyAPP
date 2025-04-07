package Modelo.EventoImportante

import java.io.Serializable

data class EventoImportante(
    var id: String = "",
    var idUsuario: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var fechaEvento: Long = 0L,             // timestamp en millis
    var notificarUnaSemanaAntes: Boolean = true
) : Serializable
