package com.example.planifyapp

import ListadoAdmin.AdminView
import ListadoAdmin.AdminViewModel
import ListadoAmant.UsuariosView
import Registro.RegistroView
import Registro.RegistroViewModel
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.ListadoAmigos.UsuariosViewModel
import com.example.makefriendsapp.Login.LoginView
import com.example.makefriendsapp.Login.LoginViewModel
import com.example.planifyapp.Estadisticas.EstadisticasView
import com.example.planifyapp.Eventos.EventosView
import com.example.planifyapp.Eventos.EventosViewModel
import com.example.planifyapp.Eventos.NuevoEvento.NuevoEventoImportanteView
import com.example.planifyapp.GestionTareas.GestionTareasView
import com.example.planifyapp.GestionTareas.NuevaTareaGeneralView
import com.example.planifyapp.Logros.NuevoLogroView
import com.example.planifyapp.Perfil.PerfilView
import com.example.planifyapp.Rutinas.DetallesRutinas.DetalleRutinaView
import com.example.planifyapp.Rutinas.NuevaRutina.NuevaRutinaView
import com.example.planifyapp.Rutinas.RutinasView
import com.example.planifyapp.Rutinas.RutinasViewModel
import com.example.planifyapp.Tareas.NuevaTareaView
import com.example.planifyapp.Usuario.GamificacionViewModel
import com.example.planifyapp.ui.theme.PlanifyAPPTheme
import android.Manifest
import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.example.planifyapp.Eventos.DetallesEventos.DetalleEventoView
import com.example.planifyapp.Logros.GestionLogrosView
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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
                    val gamificacionVM = GamificacionViewModel()


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
                            DetalleRutinaView(rutinaId, rutinasVM, gamificacionVM,navController)
                        }

                        composable(
                            route = Rutas.nuevaTareaBase,
                            arguments = listOf(navArgument("rutinaId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutinaId = backStackEntry.arguments?.getString("rutinaId") ?: ""
                            NuevaTareaView(rutinaId, rutinasVM, navController)
                        }
                        composable(Rutas.Perfil) {
                            PerfilView(
                                navController,
                                gamificacionVM)
                        }
                        composable(Rutas.Estadisticas) {
                            EstadisticasView(
                                navController,
                                gamificacionVM)
                        }
                        composable(Rutas.AdminLogros) {
                            GestionLogrosView(
                                navHostController = navController,
                                adminViewModel = AdminVM,
                                opcionElegida = { selectedItemMiOpcion = it }
                            )
                        }
                        composable(Rutas.AdminTareas) {
                            GestionTareasView(
                                navHostController = navController,
                                adminViewModel = AdminVM,
                                opcionElegida = { selectedItemMiOpcion = it }
                            )
                        }

                        composable(Rutas.NuevaTareaGeneral) {
                             NuevaTareaGeneralView(
                                navHostController = navController,
                                adminViewModel = AdminVM)
                        }

                        composable(Rutas.NuevoLogro) {
                            NuevoLogroView(
                                navHostController = navController,
                                adminViewModel = AdminVM
                            )
                        }
                        composable(
                            route = Rutas.detalleEventoBase,
                            arguments = listOf(navArgument("eventoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val eventoId = backStackEntry.arguments?.getString("eventoId") ?: ""
                            DetalleEventoView(
                                eventoId,
                                eventosVM,
                                navController)
                        }
                    }
                }
            }
        }
    }
}



fun programarNotificaciones(emailUsuario: String, context: Context) {
    val datos = Data.Builder()
        .putString("emailUsuario", emailUsuario)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<com.example.planifyapp.Auxiliar.NotificacionWorker>(15, TimeUnit.MINUTES)
        .setInputData(datos)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "NotificacionesRutinasEventos",
        androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}