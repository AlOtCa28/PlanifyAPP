package com.example.makefriendsapp.Enrutamiento

object Rutas {
    const val login = "Login"
    const val Admin = "Admin"
    const val Registro = "Registro"
    const val Usuarios = "Usuarios"
    const val Rutinas = "Rutinas"
    const val Eventos = "Eventos"
    const val NuevaRutina = "NuevaRutina"
    const val NuevoEvento = "NuevoEvento"

    // Ruta base para detalleRutina con parámetro {rutinaId}
    const val detalleRutinaBase = "detalleRutina/{rutinaId}"
    fun detalleRutina(rutinaId: String) = "detalleRutina/$rutinaId"

    // Ruta base para nuevaTarea con parámetro {rutinaId}
    const val nuevaTareaBase = "nuevaTarea/{rutinaId}"
    fun nuevaTarea(rutinaId: String) = "nuevaTarea/$rutinaId"
}