package com.example.planifyapp

import ListadoAdmin.AdminView
import ListadoAdmin.AdminViewModel
import ListadoAmant.UsuariosView
import Registro.RegistroView
import Registro.RegistroViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.ListadoAmigos.UsuariosViewModel
import com.example.makefriendsapp.Login.LoginView
import com.example.makefriendsapp.Login.LoginViewModel
import com.example.planifyapp.Eventos.EventosView
import com.example.planifyapp.Eventos.EventosViewModel
import com.example.planifyapp.Eventos.NuevoEvento.NuevoEventoImportanteView
import com.example.planifyapp.Rutinas.EditarRutinas.DetalleRutinaView
import com.example.planifyapp.Rutinas.NuevaRutina.NuevaRutinaView
import com.example.planifyapp.Rutinas.RutinasView
import com.example.planifyapp.Rutinas.RutinasViewModel
import com.example.planifyapp.Tareas.NuevaTareaView
import com.example.planifyapp.ui.theme.PlanifyAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanifyAPPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var selectedItemMiOpcion by remember { mutableStateOf("Principal") }

                    val AdminVM = AdminViewModel()
                    val usuariosVM = UsuariosViewModel()
                    val rutinasVM = RutinasViewModel()
                    val eventosVM = EventosViewModel()


                    NavHost(navController = navController, startDestination = "Login") {
                        composable(Rutas.login) {
                            LoginView(
                                navHostController = navController,
                                loginViewModel = LoginViewModel()
                            )
                        }
                        composable(Rutas.Registro) {
                            RegistroView(navController = navController, RegistroViewModel())
                        }
                        composable(Rutas.Admin){
                            AdminView(
                                navHostController = navController,
                                pantallaCargar = selectedItemMiOpcion,
                                adminViewModel = AdminVM,
                                opcionElegida = {
                                    selectedItemMiOpcion = it
                                }
                            )
                        }
                        composable(Rutas.Usuarios) {
                            UsuariosView(
                                navHostController = navController,
                                usuariosViewModel = usuariosVM,
                                opcionElegida = {
                                    selectedItemMiOpcion = it
                                }
                            )
                        }
                        composable(Rutas.Rutinas) {
                            RutinasView(
                                navHostController = navController,
                                rutinasViewModel = rutinasVM,
                                opcionElegida = {
                                    selectedItemMiOpcion = it
                                }
                            )
                        }
                        composable(Rutas.NuevaRutina) {
                            NuevaRutinaView(
                                navHostController = navController,
                                nuevaRutinaViewModel = rutinasVM
                            )
                        }
                        composable(Rutas.Eventos) {
                            EventosView(
                                navHostController = navController,
                                eventosViewModel = eventosVM,
                                opcionElegida = {
                                    selectedItemMiOpcion = it
                                }
                            )
                        }
                        composable(Rutas.NuevoEvento) {
                            NuevoEventoImportanteView(
                                navHostController = navController,
                                eventosViewModel = eventosVM,
                            )
                        }
                        composable(
                            route = Rutas.detalleRutinaBase,
                            arguments = listOf(navArgument("rutinaId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutinaId = backStackEntry.arguments?.getString("rutinaId") ?: ""
                            DetalleRutinaView(rutinaId, rutinasVM, navController)
                        }

                        composable(
                            route = Rutas.nuevaTareaBase,
                            arguments = listOf(navArgument("rutinaId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutinaId = backStackEntry.arguments?.getString("rutinaId") ?: ""
                            NuevaTareaView(rutinaId, rutinasVM, navController)
                        }
                    }
                }
            }
        }
    }
}