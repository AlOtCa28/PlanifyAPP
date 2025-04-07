package com.example.makefriendsapp.Login

import Conexion.Conexiones
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makefriendsapp.Auxiliar.Parametros
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private lateinit var firebaseauth: FirebaseAuth

    private val _correo = MutableLiveData<String>()
    var correo : LiveData<String> = _correo

    private val _contrasenia = MutableLiveData<String>()
    val contrasenia : LiveData<String> = _contrasenia

    private val _isLoginEnable = MutableLiveData<Boolean>()
    val isLoginEnable : LiveData<Boolean> = _isLoginEnable

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _showPassword = MutableLiveData<Boolean>()
    val showPassword : LiveData<Boolean> = _showPassword

    fun onShowPassword() {
        _showPassword.value = _showPassword.value?.not() ?: false
    }

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    init {
        initializeFirebaseAuth()
    }

    private fun initializeFirebaseAuth() {
        firebaseauth = FirebaseAuth.getInstance()
    }

    fun onRegistroCambiado(correo: String, contrasenia: String){
        _correo.value = correo
        _contrasenia.value = contrasenia
        _isLoginEnable.value = enableLogin(correo, contrasenia)
    }

    fun enableLogin(correo: String, contrasenia: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches() && contrasenia.length > 5
    }

    fun onUserLogged(correo: String, contrasenia: String, onUserObtained: (Boolean) -> Unit) {

        firebaseauth.signInWithEmailAndPassword(correo, contrasenia)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModelScope.launch {
                        val loginJob = async {
                            Parametros.usuarioLogged = Conexiones.obtenerUsuario(correo)
                            Parametros.usuarioLogged != null
                        }
                        val isUserObtained = loginJob.await()
                        onUserObtained(isUserObtained)
                    }
                } else {
                    onUserObtained(false)
                }
            }
    }

}