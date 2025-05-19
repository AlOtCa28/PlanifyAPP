package com.example.makefriendsapp.ListadoAmigos

import Conexion.Conexiones
import Modelo.Usuario.Usuario
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import com.example.planifyapp.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UsuariosViewModel : ViewModel() {

    private val _isMenuExpanded = MutableLiveData<Boolean>()
    val isMenuExpanded : LiveData<Boolean> = _isMenuExpanded

    private val _cv_isLongClick = MutableLiveData<Boolean>()
    val cv_isLongClick : LiveData<Boolean> = _cv_isLongClick

    private val _cv_isClick = MutableLiveData<Boolean>()
    val cv_isClick : LiveData<Boolean> = _cv_isClick

    private val _dislike = MutableLiveData<Boolean>()
    val dislike : LiveData<Boolean> = _dislike

    private val _imagenesLike = mutableStateListOf<Int>()
    val imagenesLike : SnapshotStateList<Int> = _imagenesLike

    private val _imagenesDislike = mutableStateListOf<Int>()
    val imagenesDislike : SnapshotStateList<Int> = _imagenesDislike

    private val _selectedItemOpcionMenu = MutableLiveData<OpcionMenu>()
    val selectedItemOpcionMneu : LiveData<OpcionMenu> = _selectedItemOpcionMenu

    private val _showDIalogInfoUser = MutableLiveData<Boolean>()
    val showDIalogInfoUser : LiveData<Boolean> = _showDIalogInfoUser

    private val _userSelected = MutableLiveData<Usuario>()
    val userSelected : LiveData<Usuario> = _userSelected

    private val _fotoUserSelected = MutableLiveData<Bitmap?>()
    val fotoUserSelected : LiveData<Bitmap?> = _fotoUserSelected

    private val _fotosUsers = mutableStateListOf<Bitmap>()
    val fotosUsers : SnapshotStateList<Bitmap> = _fotosUsers

    private val _btSolicitudIsEnabled = MutableLiveData<Boolean>()
    val btSolicitudIsEnabled : LiveData<Boolean> = _btSolicitudIsEnabled

    private val _fotosCargadas = MutableLiveData<Boolean>()
    val fotosCargadas : LiveData<Boolean> = _fotosCargadas

    private val _usuariosCmpatibles = mutableStateListOf<Usuario>()
    val usuariosCompatibles : SnapshotStateList<Usuario> = _usuariosCmpatibles

    private val _usuariosQueMeGustan = mutableStateListOf<String>()
    val usuariosQueMeGustan : SnapshotStateList<String> = _usuariosQueMeGustan


    fun onSelectedItemMenuChange(opcion : OpcionMenu){
        _selectedItemOpcionMenu.value = opcion
    }

    fun onSetUserSelected(user : Usuario){
        _userSelected.value = user
    }

    fun resetFotoUser(context : Context){

        val drawable = ContextCompat.getDrawable(context, R.drawable.planifyimg) ?: throw IllegalArgumentException("Invalid drawable ID")
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        _fotoUserSelected.value = bitmap

    }

    fun initializeFotosUser(context: Context, usuarios: List<Usuario>) {
        viewModelScope.launch {
            _fotosUsers.clear() // Limpiar la lista al inicio
            usuarios.forEach { usuario ->
                val foto = descargarImagenDesdeFirebase(usuario.correo)
                if (foto != null) {
                    _fotosUsers.add(foto)
                } else {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.planifyimg)
                    _fotosUsers.add(drawable?.toBitmap() ?: return@forEach)
                }
            }
        }
    }

    fun onShowDilaogClick(){
        _showDIalogInfoUser.value = true
    }

    fun onCLoseDialog(){
        _showDIalogInfoUser.value = false
    }

    fun setFotoUserSelected(foto : Bitmap){
        _fotoUserSelected.value = foto
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun descargarImagenDesdeFirebase(correo: String): Bitmap? = suspendCancellableCoroutine { continuation ->
        val localFile = File.createTempFile("tempImage", "jpeg")

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("imagenes/$correo")

        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            continuation.resume(bitmap)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }

    fun obtenerUsuarios() {
        viewModelScope.launch {
            try {
                val querySnapshot = Conexiones.obtenerUsuarios()

                if (querySnapshot != null) {
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

                        // Verificar si el usuario ya existe en la lista
                        val existeUsuario = Parametros.usuarios.any { it.correo == correo }
                        if (!existeUsuario) {
                            // Agregar usuario a la lista estática en Interventanas si no existe
                            Parametros.usuarios.add(usuario)
                        }
                    }

                    Log.e("LorenzoAD", "Usuarios -> " + Parametros.usuarios)
                } else {
                    Log.e("LorenzoAD", "No se encontraron usuarios")
                }
            } catch (e: Exception) {
                Log.e("LorenzoAD", "Error al obtener usuarios: ${e.message}")
            }
        }
    }
}