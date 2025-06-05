package com.example.planifyapp.Eventos.DetallesEventos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.planifyapp.Eventos.EventosViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoView(
    eventoId: String,
    eventosViewModel: EventosViewModel,
    navHostController: NavHostController
) {
    val eventos by eventosViewModel.eventos.collectAsState()
    val evento = eventos.find { it.id == eventoId }
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(eventoId) {
        Parametros.usuarioLogged?.correo?.let { eventosViewModel.cargarEventos(it) }
    }

    evento?.let { e ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(e.titulo, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDialog.value = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar evento", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FuchsiaLight)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = e.titulo,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = DarkBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = e.descripcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkBackground,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        Divider(color = DarkBackground.copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Fecha del evento",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = DarkBackground,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(e.fechaEvento)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkBackground
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Notificación",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = DarkBackground,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = if (e.notificarUnaSemanaAntes) "Activada (una semana antes)" else "Desactivada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkBackground
                        )
                    }
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        eventosViewModel.eliminarEvento(e.id)
                        navHostController.popBackStack()
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("¿Eliminar evento?") },
                text = { Text("Esta acción no se puede deshacer.") },
                containerColor = Color.White
            )
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
