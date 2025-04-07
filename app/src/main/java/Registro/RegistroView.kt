package Registro

import Auxiliar.Factorias
import Modelo.Usuario.Usuario
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.makefriendsapp.Enrutamiento.Rutas
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.constraintlayout.compose.ConstraintLayout
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong


@SuppressLint("ContextCastToActivity")
@Composable
fun RegistroView(navController: NavHostController
                 , registroViewModel: RegistroViewModel) {

    val correo : String by registroViewModel.correo.observeAsState(initial = "")
    val contrasenia : String by registroViewModel.contrasenia.observeAsState(initial = "")
    val nombreUser: String by registroViewModel.nombreUser.observeAsState(initial = "")
    val confContrasenia: String by registroViewModel.confContrasenia.observeAsState(initial = "")
    val foto: String by registroViewModel.fotoPerfil.observeAsState(initial = "")
    val edad: Long by registroViewModel.edad.observeAsState(initial = 18L)
    val genero : Boolean by registroViewModel.genero.observeAsState(initial = false)
    val fotoBitmap : Bitmap? by registroViewModel.fotoBitmap.observeAsState(initial = null)

    val showDialogFoto : Boolean by registroViewModel.showDialog.observeAsState(initial = false)

    val showCamara : Boolean by registroViewModel.showCamara.observeAsState(initial = false)

    var context = LocalContext.current as Activity

    val contentResolver = LocalContext.current.contentResolver

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Convert the Uri to Bitmap
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            registroViewModel.onSetFotoBitmap(bitmap)
        }
    }

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(DarkBackground)
    )
    {
        var (lblitulo, txtNombreUser,
            txtCorreo, txtContr,
            txtConfContrasenia, pbFotoPerfil,
            btRegistrar, btLimpiar,
            btSalir, btCambiarFoto,
            lblFoto, txtEdad, lblGenero, rbHombre, rbMujer,
            boxRelleno) = createRefs()

        Text(
            text = "REGISTRO",
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold), // Ajusta el tamaño del texto a 80sp
            modifier = Modifier.constrainAs(lblitulo) {
                top.linkTo(parent.top, margin = 70.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = FuchsiaStrong
        )

        txtNombreUser(modifier = Modifier.constrainAs(txtNombreUser){
            top.linkTo(lblitulo.bottom, margin = 30.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end
            )
        }, registroViewModel = registroViewModel, nombreUser = nombreUser)

        txtCorreoRegistro(modifier = Modifier.constrainAs(txtCorreo){
            top.linkTo(txtNombreUser.bottom, margin = 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        },registroViewModel = registroViewModel, correo = correo)

        txtContraseñaRegistro(modifier = Modifier.constrainAs(txtContr){
            top.linkTo(txtCorreo.bottom, margin = 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end
            )
        }, registroViewModel = registroViewModel, contrasenia = contrasenia)

        txtConfContraseña(modifier = Modifier.constrainAs(txtConfContrasenia){
            top.linkTo(txtContr.bottom, margin = 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end
            )
        }, registroViewModel = registroViewModel, confContrasenia = confContrasenia)

        txtEdad(modifier = Modifier.constrainAs(txtEdad){
            top.linkTo(txtConfContrasenia.bottom, margin = 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, registroViewModel = registroViewModel, edad = edad)

        Text(
            text = "Genero: ",
            modifier = Modifier.constrainAs(lblGenero){
                top.linkTo(txtEdad.bottom, margin =65.dp)
                start.linkTo(parent.start, margin = 65.dp)
            },
            color = FuchsiaStrong
        )

        rbHombre(
            modifier = Modifier.constrainAs(rbHombre){
                top.linkTo(txtEdad.bottom, margin = 30.dp)
                start.linkTo(lblGenero.end)
                end.linkTo(parent.end)
            },
            genero = genero
        ) {
            registroViewModel.onGeneroSelected(it)
        }

        rbMujer(
            modifier = Modifier.constrainAs(rbMujer){
                top.linkTo(rbHombre.bottom)
                start.linkTo(lblGenero.end)
                end.linkTo(parent.end, margin = 20.dp)
            },
            genero = genero
        ) {
            registroViewModel.onGeneroSelected(it)
        }

        Text(
            text = "Foto: ",
            modifier = Modifier.constrainAs(lblFoto){
                top.linkTo(lblGenero.bottom, margin = 70.dp)
                start.linkTo(parent.start, margin = 65.dp)
            },
            color = FuchsiaStrong
        )


        btCambiarFoto(
            modifier = Modifier.constrainAs(btCambiarFoto){
                top.linkTo(lblFoto.bottom, margin = 40.dp)
                start.linkTo(parent.start, margin = 50.dp)
            },
            registroViewModel = registroViewModel,
        )

        fotoRegistro(
            modifier = Modifier
                .constrainAs(pbFotoPerfil) {
                    top.linkTo(rbMujer.bottom)
                    start.linkTo(btCambiarFoto.end)
                    end.linkTo(parent.end)
                }
                .size(150.dp),
            fotoBitmap = fotoBitmap
        )

        val user = Factorias.factoriaUser(
            nombre = nombreUser,
            correo = correo,
            edad = edad,
            genero = if (genero){
                1
            }else{
                2
            },
            foto = "$correo.jpeg" // Pasa la foto aquí si la tienes
        )

        btRegistrar(
            user = user,
            registroViewModel = registroViewModel,
            modifier = Modifier.constrainAs(btRegistrar) {
                top.linkTo(btCambiarFoto.bottom, margin = 22.dp)
                start.linkTo(parent.start, margin = 30.dp)
            },
            navHostController = navController
        )

        btLimpiarCampos(modifier = Modifier.constrainAs(btLimpiar){
            start.linkTo(btRegistrar.end, margin = 20.dp)
            top.linkTo(btCambiarFoto.bottom)
            bottom.linkTo(parent.bottom, margin =20.dp)

        }, context = context){
            registroViewModel.onLimpiarCampos()
        }

        btSalir(modifier = Modifier.constrainAs(btSalir){
            start.linkTo(btLimpiar.end)
            top.linkTo(btCambiarFoto.bottom)
            bottom.linkTo(parent.bottom, margin = 20.dp)
            end.linkTo(parent.end)

        }, navHostController = navController)

        val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                registroViewModel.onShowCameraScreen(true)
            } else {
                // Handle permission denial
            }
        }

        if (showDialogFoto) {
            AlertDialogFoto(
                onDismiss = { registroViewModel.onDialogClose() },
                onCameraSelected = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onGallerySelected = {
                    galleryLauncher.launch("image/*")
                }
            )
        }

        if (showCamara){
            CameraScreen(
                registroViewModel = registroViewModel)
        }

        //Este box es simplemente de relleno para que los botones no esten por debajo de los controles del movil de la parte inferior y se puedan clickar con normalidad
        Box(
            modifier = Modifier
                .constrainAs(boxRelleno) {
                    top.linkTo(btRegistrar.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
                .size(90.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun txtNombreUser(modifier : Modifier, registroViewModel: RegistroViewModel, nombreUser : String){
    TextField(
        value = nombreUser,
        onValueChange = { registroViewModel.onNombreChanged(it) },
        modifier = modifier,
        label = { Text(text = "Nombre de usuario") },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@Composable
fun AlertDialogFoto(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Seleccione una opción") },
        text = { Text("¿Qué desea usar?") },
        confirmButton = {
            Button(onClick = {
                onCameraSelected()
                onDismiss()
            }) {
                Text("Cámara")
            }
        },
        dismissButton = {
            Button(onClick = {
                onGallerySelected()
                onDismiss()
            }) {
                Text("Galería")
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun txtCorreoRegistro(modifier : Modifier, registroViewModel: RegistroViewModel, correo : String){
    TextField(
        value = correo,
        onValueChange = { registroViewModel.onCorreoChanged(it) },
        label = { Text("Correo electrónico") },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun txtContraseñaRegistro(modifier : Modifier, registroViewModel: RegistroViewModel, contrasenia : String){
    TextField(
        value = contrasenia,
        onValueChange = { registroViewModel.onContraseniaChanged(it) },
        label = { Text("Contraseña") },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun txtConfContraseña(modifier : Modifier, registroViewModel: RegistroViewModel, confContrasenia : String){
    TextField(
        value = confContrasenia,
        onValueChange = { registroViewModel.onConfContraseniaChanged(it) },
        label = { Text("Confirmar contraseña") },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun txtEdad(modifier : Modifier, registroViewModel: RegistroViewModel, edad : Long){
    TextField(
        value = edad.toString(),
        onValueChange = { registroViewModel.onEdadChanged(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        label = { Text("Edad") },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = FuchsiaLight
        )
    )
}

@SuppressLint("ContextCastToActivity")
@Composable
fun btRegistrar(
    user: Usuario,
    registroViewModel: RegistroViewModel,
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    val context = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    Log.e("Modelo/Usuario", user.toString())

    Button(
        onClick = {
            scope.launch {

                if (registroViewModel.isMayor18())
                {
                    Log.e("Edadd", "Aqui si entra")
                    registroViewModel.onUserCreated(user)
                    Toast.makeText(context, "Usuario registrado con exito", Toast.LENGTH_SHORT).show()
                    navHostController.navigate(Rutas.login)

                    if (registroViewModel.fotoBitmap.value != null) {
                        registroViewModel.onUploadFoto(
                            user.correo,
                            registroViewModel.fotoBitmap.value!!
                        )
                    }

                }else{
                    Toast.makeText(context, "Debes de ser mayor de edada para entrar a esta aplicacion", Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ) {
        Text("Registrar")
    }
}


@Composable
private fun btLimpiarCampos(
    modifier : Modifier,
    context : Activity,
    onLimpiarCampos : () -> Unit
){
    Button(
        onClick = { onLimpiarCampos() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ){
        Text("LIMPIAR")
    }
}

@Composable
private fun btSalir(modifier : Modifier, navHostController: NavHostController){
    Button(
        onClick = { navHostController.navigate(Rutas.login) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ){
        Text("SALIR")
    }
}


@Composable
fun btCambiarFoto(
    modifier: Modifier,
    registroViewModel: RegistroViewModel,
) {

    Button(
        onClick = {
            registroViewModel.onShowDialogClick()
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = FuchsiaStrong)
    ) {
        Text(text = "CAMBIAR")
    }
}


@Composable
private fun fotoRegistro(modifier: Modifier, fotoBitmap: Bitmap?) {

    fotoBitmap?.let { rememberImagePainter(it) }?.let {
        Image(
            painter = it,
            contentDescription = "Example Image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun rbHombre(
    modifier : Modifier,
    genero : Boolean,
    onSelectionChanged : (Boolean) -> Unit
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = genero,
            onClick = { onSelectionChanged(true) },
            colors = RadioButtonDefaults.colors(
                selectedColor = FuchsiaStrong,
            )
        )
        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el RadioButton y el texto
        Text(text = "Hombre",
            color = FuchsiaStrong)
    }
}

@Composable
private fun rbMujer(
    modifier : Modifier,
    genero : Boolean,
    onSelectionChanged : (Boolean) -> Unit
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = !genero,
            onClick = { onSelectionChanged(false) },
            colors = RadioButtonDefaults.colors(
                selectedColor = FuchsiaStrong,
            )
        )
        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el RadioButton y el texto
        Text(text = "Mujer",
            color = FuchsiaStrong
        )
    }
}

@Composable
fun NoPermissionScreen(onRequestPermission: () -> Unit) {
    NoPermissionContent(
        onRequestPermission = onRequestPermission
    )
}


@Composable
fun NoPermissionContent(
    onRequestPermission: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Por favor, dame los permisos de la camara")
        Button(onClick = onRequestPermission) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "camara")
            Text(text = "Dar permisos")
        }
    }
}

@Composable
private fun CameraScreen(
    registroViewModel: RegistroViewModel,
){
    CameraContent(registroViewModel)
}

@Composable
private fun CameraContent(registroViewModel: RegistroViewModel){

    val context  = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context)}

    Scaffold(
        modifier = Modifier.fillMaxSize(), floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            registroViewModel.onSetFotoBitmap(image.toBitmap())
                            registroViewModel.onShowCameraScreen(false)
                        }
                    })

                }
            ) {

            }
        }
    ) {paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues), factory = { context ->
                PreviewView(context).apply{
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FIT_START
                }.also {
                    it.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            })
    }
}
