package ListadoAdmin

import Auxiliar.Factorias
import Modelo.Usuario.Usuario
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.makefriendsapp.Auxiliar.Parametros
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.Modelo.Menu.OpcionMenu
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@SuppressLint("ContextCastToActivity")
@Composable
fun AdminView(navHostController: NavHostController,
              adminViewModel: AdminViewModel,
              pantallaCargar : String,
              opcionElegida:(String)->Unit,
) {

    var context = LocalContext.current as Activity

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val expanded: Boolean by adminViewModel.isMenuExpanded.observeAsState(initial = false)

    val opcs = Factorias.factoriaOpcionesMenuAdmin()

    val selectedItemOpcionMenu: OpcionMenu by adminViewModel.selectedItemOpcionMneu.observeAsState(initial = opcs[0])

    val userSelected : String by adminViewModel.userSelected.observeAsState(initial = "")

    val showDialogPersona : Boolean by adminViewModel.showDialogPersona.observeAsState(initial = false)


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))

                opcs.forEach{
                    NavigationDrawerItem(
                        icon = { Icon(it.icono, contentDescription = it.opcion) },
                        label = { Text(it.opcion) },
                        selected = it.opcion == selectedItemOpcionMenu.opcion,
                        onClick = {
                            adminViewModel.onSelectedItemMenuChange(it) //Aquí obtenemos el seleccionado.
                            scope.launch {
                                drawerState.close()
                            }
                            when(selectedItemOpcionMenu.codigo){
                                0 -> {
                                    //Opcion del menu de usuarios
                                    adminViewModel.obtenerUsuarios()
                                    navHostController.navigate(Rutas.Admin)
                                }
                                1 -> {
                                    //salir de la aplicacion
                                    FirebaseAuth.getInstance().signOut()
                                    navHostController.navigate(Rutas.login)
                                }
                            }
                            opcionElegida(it.opcion)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

            }
        },
        content = {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                topBar = {
                    ToolBarListado("Lista de admin", drawerState, expanded, {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    })
                },
                bottomBar = {}
            ) {

                if (Parametros.usuarios.isNotEmpty()){
                    RVUsuariosAdmin(
                        adminViewModel = adminViewModel,
                        paddig = it
                    )
                }
            }
        }
    )
}



@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemUsuarioAdmin(
    u: Usuario,
    adminViewModel: AdminViewModel,
    onItemSeleccionado: (Usuario, Int) -> Unit
) {
    var estadoSwitch by remember { mutableStateOf(u.isActivo) }
    var estadoCBAdmin by remember { mutableStateOf(u.roles.contains(0L)) }
    var estadoCBUsuario by remember { mutableStateOf(u.roles.contains(1L)) }

    Card(
        border = BorderStroke(2.dp, color = DarkBackground),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = { onItemSeleccionado(u, 3) },
                onClick = { onItemSeleccionado(u, 1) }
            )
            .background(color = DarkBackground)
            .padding(1.dp)
    ) {
        Column(
            Modifier
                .border(BorderStroke(2.dp, Color.Black))
                .clip(shape = RoundedCornerShape(16.dp))
        ) {
            Text(
                text = u.nombreUser,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = u.nombreUser,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = estadoSwitch,
                    onCheckedChange = { newValue ->
                        estadoSwitch = newValue
                        adminViewModel.activarUsuario(u.correo, newValue)
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Checkbox(
                checked = estadoCBAdmin,
                onCheckedChange = { newValue ->
                    if (newValue) {
                        adminViewModel.addRol(u.correo, 0L)
                        u.roles.add(0L)
                    } else if (u.roles.size > 1) {
                        adminViewModel.removeRol(u.correo, 0L)
                        u.roles.remove(0L)
                    }
                    estadoCBAdmin = newValue
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Admin.")

            Checkbox(
                checked = estadoCBUsuario,
                onCheckedChange = { newValue ->
                    if (newValue) {
                        adminViewModel.addRol(u.correo, 1L)
                        u.roles.add(1L)
                    } else if (u.roles.size > 1) {
                        adminViewModel.removeRol(u.correo, 1L)
                        u.roles.remove(1L)
                    }
                    estadoCBUsuario = newValue
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "User")
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ToolBarListado(
    title: String,
    drawerState : DrawerState,
    expanded : Boolean,
    onNavigationClick: (String) -> Unit
) {
    var scope = rememberCoroutineScope()
    var exp by remember { mutableStateOf(expanded) }

    TopAppBar(
        colors = topAppBarColors(
            containerColor = DarkBackground,
            scrolledContainerColor = FuchsiaLight,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        modifier = Modifier.background(color = Color.Blue),
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
                onNavigationClick("Menu")
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú desplegable")
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RVUsuariosAdmin(
    adminViewModel: AdminViewModel,
    paddig: PaddingValues
) {
    val usuarios = Parametros.usuarios

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddig)
    ) {
        itemsIndexed(usuarios) { index, usuario ->
            ItemUsuarioAdmin(
                u = usuario,
                adminViewModel = adminViewModel,
                onItemSeleccionado = { usuario, i ->
                    when (i) {
                        1 -> {
                            // Acción para el primer caso
                            Log.d("Usuario seleccionado", "Usuario: ${usuario.nombreUser}")
                        }
                        2 -> {
                            // Acción para el segundo caso
                            Log.d("Usuario seleccionado", "Usuario: ${usuario.nombreUser}")
                        }
                        3 -> {
                            // Acción para el tercer caso
                            Log.d("Usuario seleccionado", "Usuario: ${usuario.nombreUser}")
                        }
                    }
                },
            )
        }
    }
}


