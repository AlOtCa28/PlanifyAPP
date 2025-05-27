package Modelo.TareasYLogros

import java.io.Serializable
import java.security.Timestamp

data class ProgresoLogro(
    val obtenido: Boolean = false,
    val fechaObtenido: Timestamp? = null
): Serializable
