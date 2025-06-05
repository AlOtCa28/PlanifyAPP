package com.example.planifyapp.GestionTareas

import ListadoAdmin.AdminViewModel
import Modelo.TareasYLogros.TareaGamificada
import Modelo.TareasYLogros.TareaGeneral
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionTareasView(
    navHostController: NavHostController,
    adminViewModel: AdminViewModel,
    opcionElegida: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tareas by adminViewModel.tareasGenerales.collectAsState()

    val opcionesMenu = listOf(
        OpcionMenu("Usuarios", Icons.Default.Group, 0),
        OpcionMenu("Logros", Icons.Default.EmojiEvents, 1),
        OpcionMenu("Tareas", Icons.Default.Task, 2),
        OpcionMenu("Cerrar sesión", Icons.Default.ExitToApp, 3)
    )

    var opcionSeleccionada by remember { mutableStateOf(opcionesMenu[2]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = FuchsiaLight) {
                Spacer(modifier = Modifier.height(16.dp))
                opcionesMenu.forEach { opcion ->
                    NavigationDrawerItem(
                        icon = { Icon(opcion.icono, contentDescription = opcion.opcion, tint = DarkBackground) },
                        label = { Text(opcion.opcion, color = DarkBackground) },
                        selected = opcion == opcionSeleccionada,
                        onClick = {
                            opcionSeleccionada = opcion
                            scope.launch { drawerState.close() }
                            when (opcion.codigo) {
                                0 -> {
                                    adminViewModel.obtenerUsuarios()
                                    navHostController.navigate(Rutas.Admin)
                                }
                                1 -> navHostController.navigate(Rutas.AdminLogros)
                                2 -> navHostController.navigate(Rutas.AdminTareas)
                                3 -> {
                                    FirebaseAuth.getInstance().signOut()
                                    navHostController.navigate(Rutas.login)
                                }
                            }
                            opcionElegida(opcion.opcion)
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Tareas") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Añadir Tarea") },
                    icon = { Icon(Icons.Default.Add, null) },
                    onClick = {
                        navHostController.navigate(Rutas.NuevaTareaGeneral)
                    },
                    containerColor = DarkBackground,
                    contentColor = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(FuchsiaLight)
                    .padding(16.dp)
            ) {
                LaunchedEffect(Unit) {
                    adminViewModel.cargarTareasGenerales()
                }

                LazyColumn {
                    items(tareas) { tarea ->
                        ItemTareaGeneral(tarea, adminViewModel)
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTareaGeneral(
    tarea: TareaGeneral,
    adminViewModel: AdminViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta tarea?") },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.eliminarTareaGeneral(tarea.id)
                    showDialog = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(tarea.titulo, style = MaterialTheme.typography.titleLarge)
            Text(tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
            Text("Puntos: ${tarea.puntos}", style = MaterialTheme.typography.bodySmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}



