package Modelo.Sugerencia

import java.io.Serializable


data class Sugerencia(
    val tipo: String, // "tarea" o "rutina"
    val titulo: String,
    val descripcion: String,
    val motivo: String // explicación del porqué se sugiere
): Serializable
