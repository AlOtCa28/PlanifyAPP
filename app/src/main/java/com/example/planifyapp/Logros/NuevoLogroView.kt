package com.example.planifyapp.Logros

import ListadoAdmin.AdminViewModel
import Modelo.TareasYLogros.Logro
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun NuevoLogroView(navHostController: NavHostController, adminViewModel: AdminViewModel) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var puntos by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nuevo Logro", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = puntos,
            onValueChange = { puntos = it },
            label = { Text("Puntos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val puntosInt = puntos.toIntOrNull()
            if (titulo.isNotBlank() && descripcion.isNotBlank() && puntosInt != null) {
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
        }) {
            Text("Crear logro")
        }
    }
}