package Auxiliar


import Modelo.Usuario.Usuario
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RoomPreferences
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu

object Factorias {

    fun factoriaUser(nombre: String, correo: String, edad: Long, genero: Long, foto: String): Usuario {
        val roles = arrayListOf(1L)  // Lista de roles con un rol predefinido (1L)
        val isActivo = false         // Usuario inactivo por defecto
        val isNuevo = false           // No es un usuario nuevo por defecto

        return Usuario(nombre, correo, roles, isActivo, edad, genero, foto, isNuevo)
    }

    fun factoriaOpcionesMenuAdmin(): ArrayList<OpcionMenu>{
        var titulos = arrayListOf("Usuarios",  "Salir")
        var icons = arrayListOf(
            Icons.Default.Person, Icons.Default.CalendarMonth, Icons.Default.Mail,
            Icons.Default.PeopleAlt, Icons.Default.RoomPreferences,
            Icons.AutoMirrored.Filled.ExitToApp
        )
        var opciones = ArrayList<OpcionMenu>()
        for (i in 0..<titulos.size){
            opciones.add(OpcionMenu(titulos[i], icons[i], i))
        }

        return opciones
    }

    fun getUsuario(): Usuario {
        return Usuario()
    }

}