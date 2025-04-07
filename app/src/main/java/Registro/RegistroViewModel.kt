package Registro

import Conexion.Conexiones
import Modelo.Usuario.Usuario
import android.graphics.Bitmap
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.async
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {

    private lateinit var firebaseauth: FirebaseAuth

    private val _nombreUser = MutableLiveData<String>()
    val nombreUser: LiveData<String> = _nombreUser

    private val _correo = MutableLiveData<String>()
    val correo: LiveData<String> = _correo

    private val _contrasenia = MutableLiveData<String>()
    val contrasenia: LiveData<String> = _contrasenia

    private val _confContrasenia = MutableLiveData<String>()
    val confContrasenia: LiveData<String> = _confContrasenia

    private val _fotoPerfil = MutableLiveData<String>()
    val fotoPerfil: LiveData<String> = _fotoPerfil

    private val _fotoBitmap = MutableLiveData<Bitmap?>()
    val fotoBitmap : LiveData<Bitmap?> = _fotoBitmap

    private val _isRegistroEnable = MutableLiveData<Boolean>()
    val isRegistroEnable: LiveData<Boolean> = _isRegistroEnable

    private val _edad = MutableLiveData<Long>()
    val edad: LiveData<Long> = _edad

    private val _genero = MutableLiveData<Boolean>()
    val genero : LiveData<Boolean> = _genero

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _hasCameraPermission = MutableLiveData<Boolean>()
    val hasCameraPermission : LiveData<Boolean> = _hasCameraPermission

    private val _showCamara = MutableLiveData<Boolean>()
    val showCamara : LiveData<Boolean> = _showCamara

    private val _showNoPermissionScreen = MutableLiveData<Boolean>()
    val showNoPermissionScreen : LiveData<Boolean> = _showNoPermissionScreen

    init {
        // Initialize FirebaseAuth instance
        initializeFirebaseAuth()
        updateRegistroEnableState()
        _edad.value = 18L
    }

    private fun initializeFirebaseAuth() {
        firebaseauth = FirebaseAuth.getInstance()
    }

    fun onNombreChanged(nombre: String) {
        _nombreUser.value = nombre
        updateRegistroEnableState()
    }

    fun onCorreoChanged(correo: String) {
        _correo.value = correo
        updateRegistroEnableState()
    }

    fun onContraseniaChanged(contrasenia: String) {
        _contrasenia.value = contrasenia
        updateRegistroEnableState()
    }

    fun onConfContraseniaChanged(confContrasenia: String) {
        _confContrasenia.value = confContrasenia
        updateRegistroEnableState()
    }

    fun onEdadChanged(edad : String) {

        if (edad.isNotEmpty()){
            _edad.value = edad.toLong()
        }else{
            _edad.value = 0L
        }
    }

    fun onGeneroSelected(genero : Boolean){
        _genero.value = genero
    }

    fun onFotoChanged(foto: String) {
        _fotoPerfil.value = foto
        updateRegistroEnableState()
    }

    fun onSetFotoBitmap(fotoBitmap : Bitmap?){
        _fotoBitmap.value = fotoBitmap
    }

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onShowNoPermissionScreen(valor : Boolean){
        _showNoPermissionScreen.value = valor
    }

    fun onShowCameraScreen(valor : Boolean){
        _showCamara.value = valor
    }

    fun updatePermissionCamera(valor : Boolean){
        _hasCameraPermission.value = valor
    }

    fun onLimpiarCampos(){
        _nombreUser.value = ""
        _correo.value = ""
        _contrasenia.value = ""
        _confContrasenia.value = ""
        _fotoPerfil.value = ""
        _edad.value = 18L
        _genero.value = false
        _fotoBitmap.value = null
    }


    private fun updateRegistroEnableState() {
        val nombre = _nombreUser.value ?: ""
        val correo = _correo.value ?: ""
        val contrasenia = _contrasenia.value ?: ""
        val confContrasenia = _confContrasenia.value ?: ""
        val foto = _fotoPerfil.value

        _isRegistroEnable.value = enableRegistro(nombre, correo, contrasenia, confContrasenia, foto)
    }

    private fun enableRegistro(
        nombre: String,
        correo: String,
        contrasenia: String,
        confContrasenia: String,
        foto: String?
    ): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches() &&
                nombre.isNotEmpty() &&
                contrasenia.isNotEmpty() &&
                confContrasenia.isNotEmpty() &&
                contrasenia == confContrasenia && // Comparación de contraseñas
                foto != ""
    }

    fun onUserCreated(u: Usuario) {
        viewModelScope.launch {
            val registroJob = async {
                Conexiones.registrarUsuario(u.correo, u.roles, u.edad, u.foto, u.nombreUser, genero = u.genero)
            }
            val result = registroJob.await()

            if (result) {
                initializeFirebaseAuth() // Ensure FirebaseAuth is initialized

                _correo.value?.let {
                    _contrasenia.value?.let { it1 ->
                        firebaseauth.createUserWithEmailAndPassword(it, it1)
                    }
                }

            } else {
                // Fallo en el registro
            }
        }
    }

    fun isMayor18() : Boolean{
        return _edad.value!!.toLong() >= 18L
    }

    fun onUploadFoto(correo: String, value: Bitmap) {

        viewModelScope.launch {
            val uploadFotoJob = async {
                Conexiones.subirImagenAlStorageSuspend(value, correo)
            }
            uploadFotoJob.await()
        }

    }

}

