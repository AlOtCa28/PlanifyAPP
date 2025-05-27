package com.example.planifyapp.Tareas

import Modelo.Rutina.Tarea
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.Rutinas.RutinasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTareaView(
    rutinaId: String,
    rutinasViewModel: RutinasViewModel,
    navHostController: NavHostController
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var puntos by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val puntosInt = puntos.toIntOrNull() ?: 0

                    val nuevaTarea = Tarea(
                        id = "",
                        titulo = titulo,
                        descripcion = descripcion,
                        puntos = puntosInt,
                        completada = false
                    )

                    rutinasViewModel.agregarTarea(rutinaId, nuevaTarea) { success, errorMessage ->
                        if (success) {
                            navHostController.popBackStack()
                        } else {
                            Toast.makeText(
                                navHostController.context,
                                errorMessage ?: "Error al guardar la tarea",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Guardar tarea")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = puntos,
                onValueChange = { newValue ->
                    puntos = newValue.filter { it.isDigit() }
                },
                label = { Text("Puntos") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}