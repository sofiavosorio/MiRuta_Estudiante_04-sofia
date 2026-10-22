// CARPETA ui/components: piezas reutilizables de interfaz. ActivityCard es la tarjeta de cada actividad en Home.
package com.example.miruta.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miruta.model.Actividad
import com.example.miruta.ui.theme.VerdeAccion
import com.example.miruta.ui.theme.colorPrioridad
import com.example.miruta.ui.theme.colorTextoPrioridad
import com.example.miruta.ui.theme.nombrePrioridad

// actividad = los datos que muestra la tarjeta. onClick = qué pasa al tocarla (Home abre el Detalle).
@Composable
fun ActivityCard(actividad:Actividad, onClick:()->Unit) {
    // TODO-07: mejorar diseño visual según la prioridad.
    // El fondo de la tarjeta cambia según la prioridad (colores definidos en Color.kt).
    Card(
        // clickable = la tarjeta se puede tocar. fillMaxWidth = ocupa todo el ancho.
        modifier = Modifier.fillMaxWidth().padding(vertical=5.dp).clickable(onClick=onClick),
        colors = CardDefaults.cardColors(containerColor = colorPrioridad(actividad.prioridad))
    ) {
        Column(Modifier.padding(16.dp)) {
            // Los textos van en negro porque el fondo de la tarjeta siempre es claro (también en modo oscuro).
            Text(actividad.titulo, style=MaterialTheme.typography.titleMedium, color=Color.Black, fontWeight=FontWeight.Bold)
            Text(actividad.categoria, color=Color.DarkGray)
            Text("Prioridad ${nombrePrioridad(actividad.prioridad)}", color=colorTextoPrioridad(actividad.prioridad), fontWeight=FontWeight.Bold)
            // Estado: verde si está completada, gris si está pendiente.
            Text(if (actividad.completada) "Completada" else "Pendiente", color=if (actividad.completada) VerdeAccion else Color.Gray, fontWeight=FontWeight.Bold)
        }
    }
}
