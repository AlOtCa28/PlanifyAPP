package com.example.planifyapp.Perfil.EditarPerfil

import Conexion.Conexiones
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavHostController
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong
import com.example.planifyapp.ui.theme.DarkBackground
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilView(
    navController: NavHostController,
    perfilViewModel: EditarPerfilViewModel
) {
    val contexto = LocalContext.current
    val usuarioData by perfilViewModel.usuarioActualFlow.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var edadTexto by remember { mutableStateOf("") }
    var generoSeleccionado by remember { mutableStateOf(0) }
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var camposInicializados by remember { mutableStateOf(false) }
    var yaCargado by remember { mutableStateOf(false) }

    val correoActual = FirebaseAuth.getInstance().currentUser?.email

    LaunchedEffect(correoActual) {
        if (!yaCargado && correoActual != null) {
            perfilViewModel.cargarUsuario(correoActual)
            yaCargado = true
        }
    }

    LaunchedEffect(usuarioData) {
        if (!camposInicializados && usuarioData != null) {
            nombre = usuarioData!!.nombreUser
            edadTexto = usuarioData!!.edad.toString()
            generoSeleccionado = usuarioData!!.genero.toInt()
            Conexiones.descargarImagenDesdeFirebase(usuarioData!!.correo)?.let {
                fotoBitmap = it
            }
            camposInicializados = true
        }
    }

    val launcherImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contexto.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(contexto.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            fotoBitmap = bitmap

            correoActual?.let { correo ->
                perfilViewModel.subirNuevaFoto(bitmap, correo)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(FuchsiaLight, Color.White))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(128.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(FuchsiaLight)
                        .border(4.dp, DarkBackground, CircleShape)
                        .clickable { launcherImagen.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    fotoBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Text(
                        text = "Seleccionar\nimagen",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                IconButton(
                    onClick = { launcherImagen.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                        .size(32.dp)
                        .background(DarkBackground, CircleShape)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    cursorColor = FuchsiaStrong
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = edadTexto,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        edadTexto = it
                    }
                },
                label = { Text("Edad", color = DarkBackground) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    cursorColor = FuchsiaStrong
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            val opcionesGenero = listOf("No especificado", "Masculino", "Femenino")
            var expandedGenero by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = opcionesGenero.getOrElse(generoSeleccionado) { "No especificado" },
                    onValueChange = {},
                    label = { Text("Género", color = DarkBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expandedGenero = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar género")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FuchsiaStrong,
                        unfocusedBorderColor = DarkBackground,
                        cursorColor = FuchsiaStrong
                    )
                )
                DropdownMenu(
                    expanded = expandedGenero,
                    onDismissRequest = { expandedGenero = false }
                ) {
                    opcionesGenero.forEachIndexed { index, opcion ->
                        DropdownMenuItem(
                            onClick = {
                                generoSeleccionado = index
                                expandedGenero = false
                            },
                            text = { Text(opcion, color = DarkBackground) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val edadVal = edadTexto.toLongOrNull() ?: 0L
                        if (nombre.trim().isEmpty()) {
                            Toast.makeText(contexto, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (edadVal <= 0) {
                            Toast.makeText(contexto, "Edad inválida", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val usuarioModificado = usuarioData?.copy(
                            nombreUser = nombre.trim(),
                            edad = edadVal,
                            genero = generoSeleccionado.toLong()
                        ) ?: return@Button

                        perfilViewModel.guardarCambios(usuarioModificado) { exito ->
                            if (exito) {
                                Toast.makeText(contexto, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(contexto, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBackground,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkBackground
                    ),
                    border = BorderStroke(1.dp, DarkBackground)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}



