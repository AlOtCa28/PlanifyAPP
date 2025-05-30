package com.example.planifyapp.Logros

import ListadoAdmin.AdminViewModel
import Modelo.TareasYLogros.Logro
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong

@Composable
fun NuevoLogroView(
    adminViewModel: AdminViewModel,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var puntos by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FuchsiaLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NUEVO LOGRO GENERAL",
                style = MaterialTheme.typography.titleLarge,
                color = DarkBackground
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    cursorColor = FuchsiaStrong
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    cursorColor = FuchsiaStrong
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = puntos,
                onValueChange = { puntos = it.filter { it.isDigit() } },
                label = { Text("Puntos", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    cursorColor = FuchsiaStrong
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val puntosInt = puntos.toIntOrNull() ?: 0
                        if (titulo.isNotBlank() && descripcion.isNotBlank() && puntosInt > 0) {
                            val nuevoLogro = Logro("", titulo, descripcion, puntosInt)
                            adminViewModel.crearLogro(nuevoLogro) { exito ->
                                if (exito) {
                                    Toast.makeText(context, "Logro creado correctamente", Toast.LENGTH_SHORT).show()
                                    navHostController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error al crear logro", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBackground,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }
                OutlinedButton(
                    onClick = { navHostController.popBackStack() },
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