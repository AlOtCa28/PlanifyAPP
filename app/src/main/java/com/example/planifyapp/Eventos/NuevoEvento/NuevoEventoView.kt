package com.example.planifyapp.Eventos.NuevoEvento

import Modelo.EventoImportante.EventoImportante
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planifyapp.Eventos.EventosViewModel
import com.example.planifyapp.ui.theme.DarkBackground
import com.example.planifyapp.ui.theme.FuchsiaLight
import com.example.planifyapp.ui.theme.FuchsiaStrong
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun NuevoEventoImportanteView(
    navHostController: NavHostController,
    eventosViewModel: EventosViewModel
) {
    val context = LocalContext.current
    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaEvento by remember { mutableStateOf(System.currentTimeMillis()) }
    var notificarSemanaAntes by remember { mutableStateOf(true) }

    val calendar = Calendar.getInstance().apply { timeInMillis = fechaEvento }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            fechaEvento = selectedDate.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val fechaFormateada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaEvento))

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FuchsiaLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            Text(
                "Nuevo Evento Importante",
                style = MaterialTheme.typography.titleLarge,
                color = DarkBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkBackground,
                    unfocusedBorderColor = DarkBackground,
                    focusedLabelColor = DarkBackground,
                    unfocusedLabelColor = DarkBackground,
                    cursorColor = DarkBackground
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción", color = DarkBackground) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkBackground,
                    unfocusedBorderColor = DarkBackground,
                    focusedLabelColor = DarkBackground,
                    unfocusedLabelColor = DarkBackground,
                    cursorColor = DarkBackground
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { datePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fecha: $fechaFormateada", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = notificarSemanaAntes,
                    onCheckedChange = { notificarSemanaAntes = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = DarkBackground,
                        uncheckedColor = DarkBackground
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notificar una semana antes", color = DarkBackground)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                            val nuevoEvento = EventoImportante(
                                id = "",
                                emailUsuario = email,
                                titulo = titulo,
                                descripcion = descripcion,
                                fechaEvento = fechaEvento,
                                notificarUnaSemanaAntes = notificarSemanaAntes
                            )
                            eventosViewModel.agregarEvento(nuevoEvento)
                            navHostController.popBackStack()
                        } else {
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBackground)
                ) {
                    Text("Guardar", color = Color.White)
                }

                OutlinedButton(
                    onClick = { navHostController.popBackStack() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkBackground
                    )
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
