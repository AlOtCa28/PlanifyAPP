package Auxiliar


import Modelo.Usuario.Usuario

object Factorias {

    fun factoriaUser(nombre: String, correo: String, edad: Long, genero: Long, foto: String): Usuario {
        val roles = arrayListOf(1L)  // Lista de roles con un rol predefinido (1L)
        val isActivo = false         // Usuario inactivo por defecto
        val isNuevo = false           // No es un usuario nuevo por defecto

        return Usuario(nombre, correo, roles, isActivo, edad, genero, foto, isNuevo)
    }



    fun getUsuario(): Usuario {
        return Usuario()
    }

}