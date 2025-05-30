package com.example.planifyapp.GestionTareas

import ListadoAdmin.AdminViewModel
import Modelo.TareasYLogros.TareaGeneral
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong

@Composable
fun NuevaTareaGeneralView(
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
                text = "NUEVA TAREA GENERAL",
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
                            val nuevaTarea = TareaGeneral("", titulo, descripcion, puntosInt)
                            adminViewModel.crearTarea(nuevaTarea) { exito ->
                                if (exito) {
                                    Toast.makeText(context, "Tarea creada correctamente", Toast.LENGTH_SHORT).show()
                                    navHostController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error al crear tarea", Toast.LENGTH_SHORT).show()
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