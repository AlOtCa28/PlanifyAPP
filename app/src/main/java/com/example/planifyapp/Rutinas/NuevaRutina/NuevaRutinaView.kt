package com.example.planifyapp.Rutinas.NuevaRutina

import Modelo.Rutina.Rutina
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.Rutinas.RutinasViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "DefaultLocale")
@Composable
fun NuevaRutinaView(
    navHostController: NavHostController,
    nuevaRutinaViewModel: RutinasViewModel
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf(TextFieldValue("")) }
    var descripcion by remember { mutableStateOf(TextFieldValue("")) }
    var hora by remember { mutableStateOf("") }
    val diasSeleccionados = remember { mutableStateListOf<String>() }

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    val calendar = Calendar.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FuchsiaLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Nueva Rutina",
                style = MaterialTheme.typography.titleLarge,
                color = DarkBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    focusedLabelColor = DarkBackground,
                    unfocusedLabelColor = DarkBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = FuchsiaStrong,
                    unfocusedBorderColor = DarkBackground,
                    focusedLabelColor = DarkBackground,
                    unfocusedLabelColor = DarkBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _: TimePicker, hour: Int, minute: Int ->
                            hora = String.format("%02d:%02d", hour, minute)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBackground,
                    contentColor = Color.White
                )
            ) {
                Text(if (hora.isEmpty()) "Seleccionar hora" else "Hora: $hora")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Días de repetición:", color = DarkBackground)

            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                diasSemana.forEach { dia ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .toggleable(
                                value = diasSeleccionados.contains(dia),
                                onValueChange = { isChecked ->
                                    if (isChecked) {
                                        diasSeleccionados.add(dia)
                                    } else {
                                        diasSeleccionados.remove(dia)
                                    }
                                }
                            )
                    ) {
                        Checkbox(
                            checked = diasSeleccionados.contains(dia),
                            onCheckedChange = null, // Controlado por toggleable
                            colors = CheckboxDefaults.colors(
                                checkedColor = FuchsiaStrong,
                                uncheckedColor = DarkBackground
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dia, color = DarkBackground)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val nuevaRutina = Rutina(
                            titulo = titulo.text,
                            descripcion = descripcion.text,
                            horaNotificacion = hora,
                            diasRepeticion = diasSeleccionados.toList(),
                            esActiva = true,
                            emailUsuario = FirebaseAuth.getInstance().currentUser?.email ?: ""
                        )
                        nuevaRutinaViewModel.agregarRutina(nuevaRutina)
                        navHostController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBackground,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }

                OutlinedButton(
                    onClick = {
                        navHostController.popBackStack()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkBackground
                    ),
                    border = BorderStroke(1.dp, DarkBackground)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}