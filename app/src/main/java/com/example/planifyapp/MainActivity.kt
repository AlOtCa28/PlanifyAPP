package com.example.planifyapp

import ListadoAdmin.AdminView
import ListadoAdmin.AdminViewModel
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.makefriendsapp.Enrutamiento.Rutas
import com.example.makefriendsapp.Login.LoginView
import com.example.makefriendsapp.Login.LoginViewModel
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
                    }
                }
            }
        }
    }
}