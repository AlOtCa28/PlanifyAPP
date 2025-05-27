package com.example.planifyapp.Rutinas.EditarRutinas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.Rutinas.RutinasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRutinaView(
    rutinaId: String,
    rutinasViewModel: RutinasViewModel,
    navHostController: NavHostController
) {
    val rutina by rutinasViewModel.rutinaSeleccionada.collectAsState()
    val tareas by rutinasViewModel.tareas.collectAsState()

    var rutinaCargada by remember { mutableStateOf(false) }

    LaunchedEffect(rutinaId) {
        if (!rutinaCargada) {
            rutinasViewModel.cargarRutinaPorId(rutinaId)
            rutinasViewModel.cargarTareas(rutinaId)
            rutinaCargada = true
        }
    }

    rutina?.let { r ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(r.titulo) },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navHostController.navigate("nuevaTarea/$rutinaId")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir tarea")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = r.descripcion)
                Text(text = "Hora: ${r.horaNotificacion}")
                Text(text = "Días: ${r.diasRepeticion.joinToString()}")
                Spacer(modifier = Modifier.height(16.dp))

                Text("Tareas", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(tareas) { tarea ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(tarea.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(tarea.descripcion)
                                Text("Puntos: ${tarea.puntos}")
                                Text(if (tarea.completada) "Completada" else "Pendiente")
                            }
                        }
                    }
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

