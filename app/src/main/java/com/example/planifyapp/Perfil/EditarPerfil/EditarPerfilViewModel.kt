package com.example.planifyapp.Perfil.EditarPerfil

import Conexion.Conexiones
import Conexion.Conexiones.subirImagenAlStorageSuspend
import Modelo.Usuario.Usuario
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class EditarPerfilViewModel : ViewModel() {

    private val _usuarioActualFlow = MutableStateFlow<Usuario?>(null)
    val usuarioActualFlow: StateFlow<Usuario?> get() = _usuarioActualFlow

    fun cargarUsuario(correo: String) {
        viewModelScope.launch {
            val usuario = Conexiones.obtenerUsuarioActualSuspend(correo)
            _usuarioActualFlow.value = usuario
        }
    }

    fun guardarCambios(
        usuarioModificado: Usuario,
        onCompletado: (exito: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Conexiones.guardarUsuarioFirestoreSuspend(usuarioModificado)
                _usuarioActualFlow.value = usuarioModificado // actualizar localmente
                onCompletado(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onCompletado(false)
            }
        }
    }

    fun subirNuevaFoto(bitmap: Bitmap, correo: String) {
        viewModelScope.launch {
            Conexiones.subirImagenAlStorageSuspend(bitmap, "$correo.jpeg")
        }
    }
}