package com.example.makefriendsapp.Login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("ContextCastToActivity")
@Composable
fun LoginView(navHostController: NavHostController,
              loginViewModel: LoginViewModel)
{
    val correo : String by loginViewModel.correo.observeAsState(initial = "")
    val contrasenia : String by loginViewModel.contrasenia.observeAsState(initial = "")
    val isLoginEnable: Boolean by loginViewModel.isLoginEnable.observeAsState(initial = false)

    var context = LocalContext.current as Activity

    ConstraintLayout(Modifier.fillMaxSize().background(DarkBackground)) {
        val (txtNombre, txtContrasenia,
            btLogin, btRegistro,
            lblNotRegistred,
            lblTitle, btSalir) = createRefs()

        Text(
            text = "PLANIFY APP",
            style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.constrainAs(lblTitle) {
                top.linkTo(parent.top, margin = 80.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = FuchsiaStrong
        )

        txtCorreo(modifier = Modifier.constrainAs(txtNombre) {
            top.linkTo(lblTitle.bottom, margin = 70.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, correo = correo, loginViewModel = loginViewModel, contrasenia = contrasenia)

        txtContraseña(modifier = Modifier.constrainAs(txtContrasenia) {
            top.linkTo(txtNombre.bottom, margin = 30.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, contrasenia, loginViewModel, correo)

        val scope = rememberCoroutineScope()

        btIniciarSesion(
            modifier = Modifier.constrainAs(btLogin) {
                top.linkTo(txtContrasenia.bottom, margin = 60.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            onUserLogged = { correo, contrasenia, onUserObtained ->
                loginViewModel.onUserLogged(
                    correo,
                    contrasenia,
                    onUserObtained
                )
            },
            navHostController = navHostController,
            isLoginEnable = isLoginEnable,
            correo = correo,
            contrasenia = contrasenia
        )


        Text(
            text = "¿No estas registrado?",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold), // Ajusta el tamaño del texto a 80sp
            modifier = Modifier.constrainAs(lblNotRegistred) {
                top.linkTo(btLogin.top, margin = 100.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = FuchsiaStrong
        )

        btAccederRegistro(modifier = Modifier.constrainAs(btRegistro){
            top.linkTo(lblNotRegistred.bottom, margin = 60.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, navHostController = navHostController
        )

        btSalir(modifier = Modifier.constrainAs(btSalir){
            top.linkTo(btRegistro.bottom, margin = 50.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, navHostController = navHostController, context = context
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun txtCorreo(modifier : Modifier, correo: String, loginViewModel : LoginViewModel, contrasenia: String){

    TextField(
        value = correo,
        onValueChange = { loginViewModel.onRegistroCambiado(correo = it, contrasenia = contrasenia) },
        modifier = modifier,
        label = { Text(text = "Correo electronico") },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun txtContraseña(modifier: Modifier, contrasenia: String, loginViewModel: LoginViewModel, correo: String) {
    val showPassword: Boolean by loginViewModel.showPassword.observeAsState(initial = false)

    TextField(
        value = contrasenia,
        onValueChange = { loginViewModel.onRegistroCambiado(correo = correo, contrasenia = it) },
        modifier = modifier,
        label = { Text(text = "Contraseña") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { loginViewModel.onShowPassword() }) {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Mostrar/Ocultar Contraseña"
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@Composable
fun btIniciarSesion(
    modifier: Modifier,
    onUserLogged: (String, String, (Boolean) -> Unit) -> Unit,
    navHostController: NavHostController,
    isLoginEnable: Boolean,
    correo: String,
    contrasenia: String,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            onUserLogged(correo, contrasenia) { isUserObtained ->
                if (isUserObtained) {
                    try {
                        if (Parametros.usuarioLogged!!.roles.contains(0)) {
                            navHostController.navigate(Rutas.Admin)
                        } else {
                            if (Parametros.usuarioLogged!!.isActivo) {
                                if (Parametros.usuarioLogged!!.isNuevo) {
                                } else {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Amigos encontrados", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Necesitas ser activado por un administrador", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Toast.makeText(context, "Usuario encontrado", Toast.LENGTH_SHORT).show()
                    } catch (exception: Exception) {
                        Log.e("Error", exception.message.toString())
                    }
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        },
        enabled = isLoginEnable,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ) {
        Text("INICIAR SESIÓN")
    }
}


@Composable
private fun btAccederRegistro(modifier: Modifier, navHostController: NavHostController) {
    Button(
        onClick = { navHostController.navigate(Rutas.Registro) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ) {
        Text("REGISTRARSE")
    }
}

@Composable
private fun btSalir(modifier: Modifier, navHostController: NavHostController, context: Activity) {
    Button(
        onClick = { context.finish() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ) {
        Text("SALIR")
    }
}