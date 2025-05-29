package com.example.planifyapp.Tareas

import Modelo.Rutina.Tarea
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.Rutinas.RutinasViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong


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
                text = "NUEVA TAREA",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
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
                onValueChange = { newValue -> puntos = newValue.filter { it.isDigit() } },
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
                    val nuevaTarea = Modelo.Rutina.Tarea(
                        id = "",
                        titulo = titulo,
                        descripcion = descripcion,
                        puntos = puntosInt,
                        completada = false
                    )
                    rutinasViewModel.agregarTarea(rutinaId, nuevaTarea) { success, _ ->
                        if (success) navHostController.popBackStack()
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
                    onClick = {
                        navHostController.popBackStack()
                    },
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