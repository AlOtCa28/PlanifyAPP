package Modelo.Usuario

import java.io.Serializable

data class Usuario(
    var nombreUser: String,
    var correo: String,
    var roles: ArrayList<Long>,
    var isActivo: Boolean,
    var edad: Long,
    var genero: Long,
    var foto: String,
    var isNuevo: Boolean
) : Serializable {

    // Constructor secundario vacío requerido por Firebase
    constructor() : this(
        nombreUser = "",
        correo = "",
        roles = arrayListOf(),
        edad = 0L,
        genero = 1L,
        foto = "",
        isActivo = false,
        isNuevo = false
    )

    override fun toString(): String {
        return "Usuario(nombreUser='$nombreUser', correo='$correo', roles=$roles, edad=$edad, genero=$genero, foto='$foto', isActivo=$isActivo, isNuevo=$isNuevo)"
    }
}


