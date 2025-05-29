package com.example.planifyapp.Rutinas.EditarRutinas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.Rutinas.RutinasViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRutinaView(
    rutinaId: String,
    rutinasViewModel: RutinasViewModel,
    navHostController: NavHostController
) {
    val rutina by rutinasViewModel.rutinaSeleccionada.collectAsState()
    val tareas by rutinasViewModel.tareas.collectAsState()

    LaunchedEffect(rutinaId) {
        rutinasViewModel.cargarRutinaPorId(rutinaId)
        rutinasViewModel.cargarTareas(rutinaId)
    }

    rutina?.let { r ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(r.titulo, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navHostController.navigate("nuevaTarea/$rutinaId") },
                    containerColor = DarkBackground,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir tarea")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FuchsiaLight)
                    .padding(16.dp)
            ) {
                Text(text = r.descripcion, color = DarkBackground)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Hora: ${r.horaNotificacion}", color = DarkBackground)
                Text(text = "Días: ${r.diasRepeticion.joinToString()}", color = DarkBackground)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Tareas", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(tareas) { tarea ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = androidx.compose.material3.CardDefaults.cardColors(
                                containerColor = if (tarea.completada) Color(0xFFDFF0D8) else Color.White
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(tarea.titulo, style = MaterialTheme.typography.titleMedium, color = DarkBackground)
                                Text(tarea.descripcion, color = DarkBackground)
                                Text("Puntos: ${tarea.puntos}", color = FuchsiaStrong)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (tarea.completada) "Completada ✅" else "Pendiente",
                                        color = if (tarea.completada) Color(0xFF388E3C) else DarkBackground
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    androidx.compose.material3.Switch(
                                        checked = tarea.completada,
                                        onCheckedChange = { isChecked ->
                                            rutinasViewModel.marcarTareaCompletada(tarea.id, isChecked)
                                        },
                                        colors = androidx.compose.material3.SwitchDefaults.colors(
                                            checkedThumbColor = FuchsiaStrong
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } ?: Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FuchsiaLight),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = FuchsiaStrong)
    }
}