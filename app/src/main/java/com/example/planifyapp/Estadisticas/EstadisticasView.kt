package com.example.planifyapp.Estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.planifyapp.Usuario.GamificacionViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasView(navController: NavHostController, gamificacionViewModel: GamificacionViewModel) {
    val tareas by gamificacionViewModel.tareas.collectAsState()
    val logros by gamificacionViewModel.logros.collectAsState()
    val tareasCompletadas = tareas.count { it.completada }
    val logrosObtenidos = logros.count { it.obtenido }
    val totalTareas = tareas.size
    val totalLogros = logros.size

    val usuarioEmail = FirebaseAuth.getInstance().currentUser?.email

    LaunchedEffect(usuarioEmail) {
        usuarioEmail?.let {
            gamificacionViewModel.cargarDatos(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(FuchsiaLight, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ESTADÍSTICAS",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Tareas completadas",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkBackground,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "$tareasCompletadas / $totalTareas",
                        style = MaterialTheme.typography.displaySmall,
                        color = FuchsiaLight,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Logros obtenidos",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkBackground,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "$logrosObtenidos / $totalLogros",
                        style = MaterialTheme.typography.displaySmall,
                        color = FuchsiaLight,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBackground,
                contentColor = Color.White
            )
        ) {
            Text("Volver")
        }
    }
}



