package ListadoAdmin

import Conexion.Conexiones
import Modelo.TareasYLogros.Logro
import Modelo.TareasYLogros.LogroGamificado
import Modelo.TareasYLogros.TareaGamificada
import Modelo.TareasYLogros.TareaGeneral
import Modelo.Usuario.Usuario
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {

    private val _showDialogPersona = MutableLiveData<Boolean>()
    val showDialogPersona : LiveData<Boolean> = _showDialogPersona

    private val _isMenuExpanded = MutableLiveData<Boolean>()
    val isMenuExpanded : LiveData<Boolean> = _isMenuExpanded


    private val _selectedItemOpcionMenu = MutableLiveData<OpcionMenu>()
    val selectedItemOpcionMneu : LiveData<OpcionMenu> = _selectedItemOpcionMenu

    private val _userSelected = MutableLiveData<String>()
    val userSelected : LiveData<String> = _userSelected


    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios : SnapshotStateList<Usuario> get() = _usuarios

    // Para tareas
    private val _tareasGenerales = MutableStateFlow<List<TareaGeneral>>(emptyList())
    val tareasGenerales: StateFlow<List<TareaGeneral>> = _tareasGenerales

    // Para logros
    private val _logrosGenerales = MutableStateFlow<List<Logro>>(emptyList())
    val logrosGenerales = _logrosGenerales.asStateFlow()

    private val _logrosObtenidos = MutableStateFlow<List<LogroGamificado>>(emptyList())
    val logrosObtenidos: StateFlow<List<LogroGamificado>> = _logrosObtenidos


    init {
        obtenerUsuarios()
        cargarTareasGenerales()
        cargarLogrosGenerales()
    }


    fun onSelectedItemMenuChange(opcion : OpcionMenu){
        _selectedItemOpcionMenu.value = opcion
    }

    fun activarUsuario(correo : String, valor : Boolean){
        viewModelScope.launch{
            val activarUsuario = async {

                Conexiones.activar_desactivarUsuario(correo, valor)

            }
            activarUsuario.join()
            Log.e("ActivarUsuario", "Usuario -> $correo activado con exito")
        }
    }

    fun addRol(correo : String, rol : Long){
        viewModelScope.launch{
            val addRolJob = async {

                Conexiones.addRoleToUser(correo, rol)

            }
            addRolJob.join()
        }
    }

    fun removeRol(correo : String, rol : Long){
        viewModelScope.launch{
            val removeRolJon = async {

                Conexiones.removeRoleFromUser(correo, rol)

            }
            removeRolJon.join()
        }
    }

    fun obtenerUsuarios() {
        viewModelScope.launch {
            try {
                val querySnapshot = Conexiones.obtenerUsuarios()

                if (querySnapshot != null) {
                    // Limpiar la lista antes de llenarla
                    Parametros.usuarios.clear()
                    _usuarios.clear()

                    for (document in querySnapshot.documents) {
                        val correo = document.getString("Correo") ?: ""
                        val nombreUser = document.getString("Nombre") ?: ""
                        val roles = document.get("Roles") as? ArrayList<Long> ?: arrayListOf()
                        val isActivo = document.getBoolean("Esta activo") ?: false
                        val edad = document.getLong("Edad") ?: 0L
                        val genero = document.getLong("Genero") ?: 0L
                        val foto = document.getString("Foto") ?: ""

                        val usuario = Usuario(
                            nombreUser = nombreUser,
                            correo = correo,
                            roles = roles,
                            isActivo = isActivo,
                            edad = edad,
                            genero = genero,
                            foto = foto
                        )

                        // Agregar usuario a la lista estática en Interventanas
                        Parametros.usuarios.add(usuario)
                        _usuarios.add(usuario)
                    }

                    Log.e("Alvaro", "Usuarios -> " + Parametros.usuarios)
                } else {
                    Log.e("Alvaro", "No se encontraron usuarios")
                }
            } catch (e: Exception) {
                Log.e("Alvaro", "Error al obtener usuarios: ${e.message}")
            }
        }
    }

    fun cargarTareasGenerales() {
        viewModelScope.launch {
            try {
                val querySnapshot = Conexiones.obtenerTareasGenerales().await()
                val tareas = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(TareaGeneral::class.java)?.copy(id = doc.id)
                }
                _tareasGenerales.value = tareas
            } catch (e: Exception) {
                Log.e("AdminTareasVM", "Error al cargar tareas generales: ${e.message}")
            }
        }
    }

    fun cargarLogrosObtenidos(correo: String) {
        Conexiones.obtenerLogrosObtenidos(correo) { lista ->
            _logrosObtenidos.value = lista
        }
    }


    fun cargarLogrosGenerales() {
        Conexiones.obtenerLogrosGenerales { lista ->
            _logrosGenerales.value = lista
        }
    }



    fun crearLogro(logro: Logro, onComplete: (Boolean) -> Unit) {
        val docRef = Conexiones.db.collection("LogrosGenerales").document()
        val logroConId = logro.copy(id = docRef.id)
        docRef.set(logroConId)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                Log.e("AdminTareasVM", "Error creando logro: ${it.message}")
                onComplete(false)
            }
    }

    fun eliminarLogro(id: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Conexiones.eliminarLogroPorId(id).await()
                cargarLogrosGenerales()
                onComplete(true)
            } catch (e: Exception) {
                Log.e("AdminTareasVM", "Error al eliminar logro: ${e.message}")
                onComplete(false)
            }
        }
    }

    fun crearTarea(tarea: TareaGeneral, onComplete: (Boolean) -> Unit) {
        val docRef = Conexiones.db.collection("TareasGenerales").document()
        val tareaConId = tarea.copy(id = docRef.id)
        docRef.set(tareaConId)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                Log.e("AdminTareasVM", "Error creando tarea: ${it.message}")
                onComplete(false)
            }
    }

    fun eliminarTareaGeneral(id: String) {
        viewModelScope.launch {
            try {
                Conexiones.eliminarTareaGeneral(id).await()
                cargarTareasGenerales()
            } catch (e: Exception) {
                Log.e("AdminTareasVM", "Error al eliminar tarea: ${e.message}")
            }
        }
    }
}