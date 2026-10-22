// Pantalla 4: DETALLE. Muestra toda la información de una actividad y permite cambiar su estado.
package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.miruta.ui.theme.colorPrioridad
import com.example.miruta.ui.theme.colorTextoPrioridad
import com.example.miruta.ui.theme.nombrePrioridad
import com.example.miruta.viewmodel.ActivityViewModel

// id = viene de Home a través de la ruta "detail/{id}" (lo lee AppNavigation).
@Composable
fun DetailScreen(id:Int, vm:ActivityViewModel, onVolver:()->Unit) {
    // Se observa la lista completa: si cambia el estado, esta pantalla se actualiza sola.
    val lista by vm.actividades.collectAsStateWithLifecycle()
    // TODO-11: mejorar manejo de datos inválidos y diseño.
    // Si el id no es válido (menor que 1) o no existe en la lista, actividad queda en null.
    val actividad = if (id < 1) null else lista.find { it.id==id }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("Detalle de actividad", style=MaterialTheme.typography.headlineSmall)
        if(actividad==null) {
            // Dato inválido: no se rompe la app, se muestra un mensaje.
            Text("Actividad no encontrada", color=MaterialTheme.colorScheme.error)
            Text("Es posible que el identificador no sea válido. Regresa a Inicio y vuelve a intentarlo.")
        }
        else {
            // Tarjeta con el mismo color de prioridad que en Home.
            Card(Modifier.fillMaxWidth(), colors=CardDefaults.cardColors(containerColor=colorPrioridad(actividad.prioridad))) {
                Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text(actividad.titulo, style=MaterialTheme.typography.titleMedium, color=Color.Black, fontWeight=FontWeight.Bold)
                    // La descripción es opcional: si está vacía se muestra "Sin descripción".
                    Text(if (actividad.descripcion.isBlank()) "Sin descripción" else actividad.descripcion, color=Color.Black)
                    Text("Categoría: ${actividad.categoria}", color=Color.Black)
                    Text("Prioridad: ${nombrePrioridad(actividad.prioridad)}", color=colorTextoPrioridad(actividad.prioridad), fontWeight=FontWeight.Bold)
                    Text(if(actividad.completada) "Estado: Completada" else "Estado: Pendiente", color=Color.Black)
                }
            }
            // Le pide al ViewModel que cambie el estado. El texto del botón dice lo que va a hacer.
            Button(onClick={vm.cambiarEstado(id)}) { Text(if(actividad.completada) "Marcar como pendiente" else "Marcar como completada") }
        }
        OutlinedButton(onClick=onVolver) { Text("Volver") }
    }
}
