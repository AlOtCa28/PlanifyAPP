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
    const val Estadisticas = "Estadisticas"
    const val Perfil = "Perfil"
    const val AdminTareas = "AdminTareas"
    const val AdminLogros = "AdminLogros"
    const val NuevaTareaGeneral = "NuevaTarea"
    const val NuevoLogro = "NuevoLogro"


    // Ruta base para detalleEvento con parámetro {eventoId}
    const val detalleEventoBase = "detalleEvento/{eventoId}"
    fun detalleEvento(eventoId: String) = "detalleEvento/$eventoId"


    // Ruta base para detalleRutina con parámetro {rutinaId}
    const val detalleRutinaBase = "detalleRutina/{rutinaId}"
    fun detalleRutina(rutinaId: String) = "detalleRutina/$rutinaId"

    // Ruta base para nuevaTarea con parámetro {rutinaId}
    const val nuevaTareaBase = "nuevaTarea/{rutinaId}"
    fun nuevaTarea(rutinaId: String) = "nuevaTarea/$rutinaId"
}