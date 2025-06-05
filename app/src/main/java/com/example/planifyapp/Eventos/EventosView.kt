package com.example.planifyapp.Eventos

import Modelo.EventoImportante.EventoImportante
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosView(
    navHostController: NavHostController,
    eventosViewModel: EventosViewModel,
    opcionElegida: (String) -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val eventos by eventosViewModel.eventos.collectAsState()
    val emailUsuario = Parametros.usuarioLogged?.correo

    val opcionesMenu = listOf(
        OpcionMenu("Principal", Icons.Default.Home, 0),
        OpcionMenu("Rutinas", Icons.Default.List, 1),
        OpcionMenu("Cerrar sesión", Icons.Default.ExitToApp, 2)
    )

    var opcionSeleccionada by remember { mutableStateOf(opcionesMenu[0]) }

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
                                0 -> navHostController.navigate(Rutas.Usuarios)
                                1 -> navHostController.navigate(Rutas.Rutinas)
                                2 -> {
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    title = { Text("Eventos") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Añadir") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = { navHostController.navigate(Rutas.NuevoEvento) },
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
                LaunchedEffect(emailUsuario) {
                    emailUsuario?.let {
                        eventosViewModel.cargarEventos(it)
                    }
                }

                LazyColumn {
                    items(eventos) { evento ->
                        ItemEvento(evento, eventosViewModel, navHostController)
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // espacio para que FAB no tape último ítem
                    }
                }
            }
        }
    }
}

@Composable
fun ItemEvento(evento: EventoImportante, eventosViewModel: EventosViewModel,navHostController: NavHostController) {
    var estadoNotificacion by remember { mutableStateOf(evento.notificarUnaSemanaAntes) }

    LaunchedEffect(evento.notificarUnaSemanaAntes) {
        estadoNotificacion = evento.notificarUnaSemanaAntes
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navHostController.navigate(Rutas.detalleEvento(evento.id))  // Navegar al detalle del evento
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = evento.titulo, style = MaterialTheme.typography.titleLarge)
            Text(text = evento.descripcion, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Date(evento.fechaEvento)
                )}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = if (estadoNotificacion) "Notificar una semana antes" else "No notificar")
                Switch(
                    checked = estadoNotificacion,
                    onCheckedChange = { nuevoEstado ->
                        estadoNotificacion = nuevoEstado
                        eventosViewModel.actualizarNotificacionEvento(evento, nuevoEstado)
                    }
                )
            }
        }
    }
}