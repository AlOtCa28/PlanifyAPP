package Modelo.TareasYLogros

import java.io.Serializable
import java.security.Timestamp

data class ProgresoTarea(
    val completada: Boolean = false,
    val fechaCompletada: Timestamp? = null
): Serializable
