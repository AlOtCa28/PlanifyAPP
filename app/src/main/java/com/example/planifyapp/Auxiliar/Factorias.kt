package Auxiliar


import Modelo.Usuario.Usuario
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RoomPreferences
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import java.util.Calendar

object Factorias {

    fun factoriaUser(nombre: String, correo: String, edad: Long, genero: Long, foto: String): Usuario {
        val roles = arrayListOf(1L)  // Lista de roles con un rol predefinido (1L)
        val isActivo = false         // Usuario inactivo por defecto

        return Usuario(nombre, correo, roles, isActivo, edad, genero, foto)
    }


    fun factoriaOpcionesMenuAdmin(): ArrayList<OpcionMenu>{
        var titulos = arrayListOf("Usuarios", "Logros", "Tareas" ,"Salir")
        var icons = arrayListOf(
            Icons.Default.Person,
            Icons.Default.EmojiEvents,
            Icons.Default.Task,
            Icons.AutoMirrored.Filled.ExitToApp
        )
        var opciones = ArrayList<OpcionMenu>()
        for (i in 0..<titulos.size){
            opciones.add(OpcionMenu(titulos[i], icons[i], i))
        }

        return opciones
    }

    fun diaANombre(numero: Int): String {
        return when (numero) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> "Desconocido"
        }
    }
}