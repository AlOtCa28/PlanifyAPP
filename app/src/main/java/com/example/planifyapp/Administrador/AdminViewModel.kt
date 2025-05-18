package ListadoAdmin

import Conexion.Conexiones
import Modelo.Usuario.Usuario
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {

    private val _cv_isLongClick = MutableLiveData<Boolean>()
    val cv_isLongClick : LiveData<Boolean> = _cv_isLongClick

    private val _cv_isClick = MutableLiveData<Boolean>()
    val cv_isClick : LiveData<Boolean> = _cv_isClick

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _showDialogPersona = MutableLiveData<Boolean>()
    val showDialogPersona : LiveData<Boolean> = _showDialogPersona

    private val _isMenuExpanded = MutableLiveData<Boolean>()
    val isMenuExpanded : LiveData<Boolean> = _isMenuExpanded

    private val _switchStates = mutableStateListOf<Boolean>()
    val switchStates: List<Boolean> get() = _switchStates

    private val _cbAdminStaes = mutableStateListOf<Boolean>()
    val cbAdminStates : List<Boolean> get() = _cbAdminStaes

    private val _cbAmanteStates = mutableStateListOf<Boolean>()
    val cbAmanteStates : List<Boolean> get() = _cbAmanteStates

    private val _isCBAmanteCheckedMap = MutableLiveData<Boolean>()
    val isCBAmanteCheckedMap : LiveData<Boolean>  = _isCBAmanteCheckedMap

    private val _selectedItemOpcionMenu = MutableLiveData<OpcionMenu>()
    val selectedItemOpcionMneu : LiveData<OpcionMenu> = _selectedItemOpcionMenu

    private val _userSelected = MutableLiveData<String>()
    val userSelected : LiveData<String> = _userSelected

    private val _userDialog = MutableLiveData<Usuario>()
    val userDialog : LiveData<Usuario> = _userDialog

    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios : SnapshotStateList<Usuario> get() = _usuarios

    init {
        obtenerUsuarios()

    }

    fun initializeSwitchStates(usuarios: List<Usuario>) {
        _switchStates.clear()
        usuarios.forEach { usuario ->
            _switchStates.add(usuario.isActivo)
        }
    }

    fun initializeCBAdminStates(usuarios: List<Usuario>){
        _cbAdminStaes.clear()
        usuarios.forEach { usuario ->
            _cbAdminStaes.add(usuario.roles.contains(0))
        }
    }

    fun initializeCBAmanteStates(usuarios: List<Usuario>){
        _cbAmanteStates.clear()
        usuarios.forEach { usuario ->
            _cbAmanteStates.add(usuario.roles.contains(1))
        }
    }

    fun onSelectedItemMenuChange(opcion : OpcionMenu){
        _selectedItemOpcionMenu.value = opcion
    }

    fun onUserSelected(user : String){
        _userSelected.value = user
    }

    fun onExpandMenu(){
        _isMenuExpanded.value = true
    }

    fun onLongClick(){
        _cv_isLongClick.value = true
    }

    fun onClick(){
        _cv_isClick.value = true
    }

    fun onCBAmanteChangeValue(i : Int, valor: Boolean) {
        _isCBAmanteCheckedMap.value = valor
    }

    fun onShowDilaog(){
        _showDialog.value = true
    }

    fun onCloseDialog(){
        _showDialog.value = false
    }

    fun onShowDialogPersona(){
        _showDialogPersona.value = true
    }

    fun onCloseDialogPersona(){
        _showDialogPersona.value = false
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

    fun setUserDialog(u : Usuario){
        _userDialog.value = u
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
                        val preferenciasMap = document.get("Preferencias") as? ArrayList<String> ?: arrayListOf()

                        val isNuevo = document.getBoolean("Es nuevo") ?: false

                        val usuario = Usuario(
                            nombreUser = nombreUser,
                            correo = correo,
                            roles = roles,
                            isActivo = isActivo,
                            edad = edad,
                            genero = genero,
                            foto = foto,
                            isNuevo = isNuevo
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

    fun updateSwitchState(index: Int, value: Boolean) {
        _switchStates[index] = value
    }

    fun updateCBAdminState(index: Int, value: Boolean){
        _cbAdminStaes[index] = value
    }

    fun updateCBAmanteState(index: Int, value: Boolean){
        _cbAmanteStates[index] = value
    }

    fun eliminarUsuario(nombre: String, callback: (Boolean) -> Unit) {
        Conexiones.eliminarUsuarioPorNombre(nombre){
            callback(it)

        }
    }

}