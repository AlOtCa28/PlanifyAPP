package ListadoAmant

import Modelo.TareasYLogros.TareaGamificada
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.ListadoAmigos.UsuariosViewModel
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import com.example.planifyapp.Usuario.GamificacionViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosView(
    navHostController: NavHostController,
    usuariosViewModel: UsuariosViewModel,
    opcionElegida: (String) -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val opcionesMenu = listOf(
        OpcionMenu("Rutinas", Icons.Default.List, 0),
        OpcionMenu("Eventos", Icons.Default.Event, 1),
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
                                0 -> navHostController.navigate(Rutas.Rutinas)
                                1 -> navHostController.navigate(Rutas.Eventos)
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
                    title = { Text("PlanifyAPP") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Perfil") },
                                onClick = {
                                    expanded = false
                                    navHostController.navigate(Rutas.Perfil)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Estadísticas") },
                                onClick = {
                                    expanded = false
                                    navHostController.navigate(Rutas.Estadisticas)
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            val gamificacionViewModel: GamificacionViewModel = viewModel()
            val tareas by gamificacionViewModel.tareas.collectAsState()
            val puntosTotales by gamificacionViewModel.puntosTotales.collectAsState()
            val emailUsuario = FirebaseAuth.getInstance().currentUser?.email ?: ""

            LaunchedEffect(emailUsuario) {
                if (emailUsuario.isNotEmpty()) {
                    gamificacionViewModel.cargarDatos(emailUsuario)
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(FuchsiaLight)
                    .padding(16.dp)
            ) {
                Text("Bienvenido a PlanifyAPP", color = DarkBackground, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aquí podrás gestionar tus rutinas y eventos.", color = DarkBackground, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))

                TablonDeTareas(
                    tareas = tareas,
                    onCompletarTarea = { tarea ->
                        gamificacionViewModel.completarTarea(emailUsuario, tarea)
                    }
                )
            }
        }
    }
}


@Composable
fun TablonDeTareas(
    tareas: List<TareaGamificada>,
    onCompletarTarea: (TareaGamificada) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Tablón de tareas",
            style = MaterialTheme.typography.titleMedium,
            color = DarkBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        tareas.forEach { tarea ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (tarea.completada) Color(0xFFDFF0D8) else Color.White
                )
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text(tarea.titulo, style = MaterialTheme.typography.titleSmall)
                    Text(tarea.descripcion, style = MaterialTheme.typography.bodySmall)
                    if (!tarea.completada) {
                        Button(
                            onClick = {  onCompletarTarea(tarea)  },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Completar (+${tarea.puntos} pts)")
                        }
                    } else {
                        Text("Completada ✅", color = Color.Green, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}



